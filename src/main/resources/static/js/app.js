document.addEventListener('DOMContentLoaded', function () {

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
        if (link) {
            window.location.href =
                link.getAttribute('data-navigate');
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
        if (e.target.matches('[data-vendor-change]')) {
            onVendorChange(e.target);
            return;
        }

        if (e.target.matches('[data-page-size]')) {
            var basePath =
                e.target.getAttribute('data-page-size');
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

    var actionCells =
        document.querySelectorAll('td.actions');
    actionCells.forEach(function (td) {
        td.addEventListener('click', function (e) {
            e.stopPropagation();
        });
    });
});
