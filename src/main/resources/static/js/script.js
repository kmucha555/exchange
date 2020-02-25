$(document).ready(function () {
    const url = window.location.href + '/currencies';
    let publicationDate;
    $.fn.dataTable.ext.errMode = 'none';
    const currencies = $('#currencies')
        .on('error.dt', () => {
            $("div.toolbar").html(`<p class="text-danger">System temporary unavailable</p>`);
            $("#currencies").prop("disabled", true);
        })
        .DataTable({
            'order': [[0, "asc"]],
            'paging': false,
            'ordering': true,
            'info': false,
            'searching': false,
            'scrollX': false,
            'scrollY': false,
            'dom': '<"toolbar">frtip',
            'columnDefs': [
                {"className": "dt-center", "targets": "_all"}
            ],
            ajax: {
                url: url,
                dataSrc: json => {
                    const returnData = [];
                    publicationDate = new Date(json.publicationDate);
                    $("div.toolbar").html(`<small>Last updated: ${publicationDate.toLocaleDateString()} ${publicationDate.toLocaleTimeString()}</small>`);
                    json.items.forEach(element =>
                        returnData.push(
                            {
                                'code': element.code,
                                'unit': element.unit,
                                'sellPrice': element.sellPrice,
                                'action': `<a class="btn-sm btn-warning" href="/transaction/buy/${element.id}">Buy</a>`
                            }
                        )
                    );
                    return returnData;
                }
            },
            columns: [
                {data: 'code'},
                {data: 'unit'},
                {data: 'sellPrice'},
                {data: 'action'}
            ]
        });
    setInterval(() => currencies.ajax.reload(), 5000);
});
