document.addEventListener('DOMContentLoaded', function () {

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
        if (link && !e.target.closest('td.actions')) {
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
            var msg =
                del.getAttribute('data-confirm-submit');
            if (!confirm(msg)) {
                e.preventDefault();
            }
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
