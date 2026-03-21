/* Apply saved theme before DOM renders to prevent flash */
(function () {
    var theme = localStorage.getItem('theme');
    if (theme === 'dark') {
        document.documentElement.setAttribute(
            'data-theme', 'dark');
    }
})();

document.addEventListener('DOMContentLoaded', function () {

    /* --- Theme toggle --- */
    var toggle = document.getElementById('theme-toggle');
    if (toggle) {
        function updateToggleLabel() {
            var isDark = document.documentElement
                .getAttribute('data-theme') === 'dark';
            toggle.textContent = isDark ? '\u2600' : '\u263D';
            toggle.setAttribute('title',
                isDark ? 'Switch to light mode'
                       : 'Switch to dark mode');
        }
        updateToggleLabel();

        toggle.addEventListener('click', function () {
            var isDark = document.documentElement
                .getAttribute('data-theme') === 'dark';
            if (isDark) {
                document.documentElement
                    .removeAttribute('data-theme');
                localStorage.setItem('theme', 'light');
            } else {
                document.documentElement
                    .setAttribute('data-theme', 'dark');
                localStorage.setItem('theme', 'dark');
            }
            updateToggleLabel();
        });
    }

    document.querySelectorAll('.toast').forEach(
        function (toast) {
            setTimeout(function () {
                toast.remove();
            }, 3000);
        });

    var pageSizeSelect =
        document.querySelector('[data-page-size]');
    if (pageSizeSelect) {
        var key = 'pageSize.'
            + pageSizeSelect.getAttribute('data-page-size')
                .replace(/^\//, '');
        var params = new URLSearchParams(
            window.location.search);
        if (!params.has('size')) {
            var stored = localStorage.getItem(key);
            if (stored) {
                window.location.replace(
                    window.location.pathname
                        + '?size=' + stored);
                return;
            }
        }
    }

    function toggleForm(id) {
        var row = document.getElementById(id);
        if (!row) {
            return;
        }
        var showing = row.style.display === 'none';
        row.style.display = showing ? '' : 'none';
        if (showing) {
            var dateInput =
                row.querySelector('input[type="date"]');
            if (dateInput && !dateInput.value) {
                dateInput.value =
                    new Date().toISOString().slice(0, 10);
            }
        }
    }

    function onVendorChange(select) {
        var row = select.closest('tr');
        var fields =
            row.querySelector('.new-vendor-fields');
        fields.style.display =
            select.value === '__new__' ? '' : 'none';
        if (select.value !== '__new__') {
            fields.querySelectorAll('input')
                .forEach(function (i) { i.value = ''; });
        }
    }

    document.body.addEventListener('click', function (e) {
        var uploadTarget =
            e.target.closest('[data-image-upload]');
        if (uploadTarget) {
            var fileInput =
                uploadTarget.querySelector('.file-input-hidden');
            if (fileInput) {
                fileInput.click();
            }
            return;
        }

        var btn = e.target.closest('[data-toggle-form]');
        if (btn) {
            toggleForm(btn.getAttribute('data-toggle-form'));
            return;
        }

        var target =
            e.target.closest('[data-target]');
        if (target) {
            toggleForm(
                target.getAttribute('data-target'));
            return;
        }

        var link =
            e.target.closest('[data-navigate]');
        if (link && !e.target.closest('td.actions')
                && !e.target.closest('td.bulk-check')) {
            window.location.href =
                link.getAttribute('data-navigate');
            return;
        }

        var addPhone =
            e.target.closest('.btn-add-alt-phone');
        if (addPhone) {
            var section =
                addPhone.closest('.alt-phones-section');
            var template =
                section.querySelector('.alt-phone-template');
            var clone = template.cloneNode(true);
            clone.style.display = '';
            clone.classList.remove('alt-phone-template');
            clone.classList.add('alt-phone-row');
            template.parentNode.insertBefore(
                clone, template);
            return;
        }

        var removePhone =
            e.target.closest('.btn-remove-alt-phone');
        if (removePhone) {
            var row = removePhone.closest(
                '.alt-phone-row');
            if (row) {
                row.remove();
            }
            return;
        }

        var del =
            e.target.closest('[data-confirm-submit]');
        if (del) {
            e.preventDefault();
            showConfirmDialog(del);
        }
    });

    document.body.addEventListener('change', function (e) {
        if (e.target.matches('[data-auto-submit]')) {
            var form = e.target.closest('form');
            if (form && e.target.files.length > 0) {
                form.submit();
            }
            return;
        }

        if (e.target.matches('[data-vendor-change]')) {
            onVendorChange(e.target);
            return;
        }

        if (e.target.matches('[data-page-size]')) {
            var basePath =
                e.target.getAttribute('data-page-size');
            var storeKey = 'pageSize.'
                + basePath.replace(/^\//, '');
            localStorage.setItem(
                storeKey, e.target.value);
            window.location.href =
                basePath + '?size=' + e.target.value;
        }
    });

    function showConfirmDialog(trigger) {
        var msg =
            trigger.getAttribute('data-confirm-submit');
        var name =
            trigger.getAttribute('data-confirm-name');
        var overlay =
            document.createElement('div');
        overlay.className = 'confirm-overlay';
        var card = document.createElement('div');
        card.className = 'confirm-card';
        var isDelete =
            msg.toLowerCase().indexOf('delete') === 0;
        var action = isDelete ? 'Delete' : 'Skip';
        var title = document.createElement('h3');
        title.className = 'confirm-title';
        title.textContent = name
            ? action + ' ' + name + '?'
            : 'Are you sure?';
        card.appendChild(title);
        var body = document.createElement('p');
        body.className = 'confirm-body';
        body.textContent = msg;
        card.appendChild(body);
        var actions = document.createElement('div');
        actions.className = 'confirm-actions';
        var cancelBtn =
            document.createElement('button');
        cancelBtn.type = 'button';
        cancelBtn.className = 'confirm-cancel';
        cancelBtn.textContent = 'Cancel';
        actions.appendChild(cancelBtn);
        var confirmBtn =
            document.createElement('button');
        confirmBtn.type = 'button';
        confirmBtn.className = 'confirm-delete';
        confirmBtn.textContent = action;
        actions.appendChild(confirmBtn);
        card.appendChild(actions);
        overlay.appendChild(card);
        document.body.appendChild(overlay);
        confirmBtn.focus();
        cancelBtn.addEventListener('click', function () {
            overlay.remove();
        });
        overlay.addEventListener('click', function (ev) {
            if (ev.target === overlay) {
                overlay.remove();
            }
        });
        confirmBtn.addEventListener('click', function () {
            overlay.remove();
            var form = trigger.closest('form');
            if (form) {
                form.submit();
            }
        });
    }

    /* --- Bulk selection --- */
    var selectAll =
        document.getElementById('select-all');
    if (selectAll) {
        selectAll.addEventListener('change', function () {
            var boxes = document.querySelectorAll(
                '.item-checkbox');
            boxes.forEach(function (cb) {
                cb.checked = selectAll.checked;
            });
            updateBulkToolbar();
        });
        document.body.addEventListener('change',
            function (e) {
                if (e.target.classList
                        .contains('item-checkbox')) {
                    updateBulkToolbar();
                }
            });
    }

    function getSelectedIds() {
        var ids = [];
        document.querySelectorAll(
            '.item-checkbox:checked'
        ).forEach(function (cb) {
            ids.push(cb.value);
        });
        return ids;
    }

    function updateBulkToolbar() {
        var ids = getSelectedIds();
        var toolbar =
            document.getElementById('bulk-toolbar');
        if (!toolbar) {
            return;
        }
        if (ids.length > 0) {
            toolbar.style.display = '';
            document.getElementById('bulk-count')
                .textContent = ids.length + ' selected';
        } else {
            toolbar.style.display = 'none';
        }
    }

    document.body.addEventListener('submit', function (e) {
        var bulkDelete =
            e.target.id === 'bulk-delete-form';
        var bulkCategory =
            e.target.id === 'bulk-category-form';
        if (bulkDelete || bulkCategory) {
            var ids = getSelectedIds();
            if (ids.length === 0) {
                e.preventDefault();
                return;
            }
            var idField = bulkDelete
                ? 'bulk-delete-ids'
                : 'bulk-category-ids';
            document.getElementById(idField)
                .value = ids.join(',');
            if (bulkDelete) {
                var btn = e.target.querySelector(
                    '[data-confirm-submit]');
                if (btn) {
                    btn.setAttribute(
                        'data-confirm-submit',
                        'Delete ' + ids.length
                            + ' selected item(s)'
                            + ' and all their'
                            + ' schedules and'
                            + ' records?');
                }
            }
        }
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            var overlay =
                document.querySelector('.confirm-overlay');
            if (overlay) {
                overlay.remove();
                return;
            }
            document.querySelectorAll(
                '.form-row, .add-form'
            ).forEach(function (el) {
                if (el.style.display !== 'none') {
                    el.style.display = 'none';
                }
            });
            return;
        }

        var tag = document.activeElement.tagName;
        var isInput = tag === 'INPUT'
            || tag === 'TEXTAREA' || tag === 'SELECT';
        if (e.key === '/' && !isInput) {
            var searchInput =
                document.querySelector('#search input');
            if (searchInput) {
                e.preventDefault();
                searchInput.focus();
            }
        }
    });

    document.body.addEventListener('submit', function (e) {
        var form = e.target.closest('[data-open-window]');
        if (form) {
            e.preventDefault();
            var itemId = form.itemId.value;
            if (itemId) {
                var url =
                    form.getAttribute('data-open-window');
                window.open(
                    url + '?itemId=' + itemId, '_blank');
            }
        }
    });

});
