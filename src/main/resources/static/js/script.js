$(document).ready(function () {
    let publicationDate;
    $.fn.dataTable.ext.errMode = 'none';

    const currencies = $('#currencies')
        .on('error.dt', () => {
            $("#lastUpdate").html(`<p class="text-danger">System temporary unavailable</p>`);
            $("#currencies").find('a').hide();
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
                url: window.location.href + '/currencies',
                dataSrc: json => {
                    const returnData = [];
                    publicationDate = new Date(json.publicationDate);
                    $("#lastUpdate").html(`Last updated: ${publicationDate.toLocaleDateString()} ${publicationDate.toLocaleTimeString()}`);
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

    const wallet = $('#wallet')
        .on('error.dt', () => {
            $("#lastUpdate").html(`<p class="text-danger">System temporary unavailable</p>`);
            $("#wallet").find('a').hide();
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
                url: window.location.href + '/wallet',
                dataSrc: json => {
                    const returnData = [];
                    json.forEach(element =>
                        returnData.push(
                            {
                                'code': element.code,
                                'amount': element.amount,
                                'purchasePrice': element.purchasePrice,
                                'value': (element.amount * element.purchasePrice).toFixed(2),
                                'action': `<a class="btn-sm btn-danger" href="/transaction/sell/${element.currencyRateId}">Sell</a>`
                            }
                        )
                    );
                    return returnData;
                }
            },
            columns: [
                {data: 'code'},
                {data: 'purchasePrice'},
                {data: 'amount'},
                {data: 'value'},
                {data: 'action'}
            ]
        });

    setInterval(() => {
        currencies.ajax.reload();
        wallet.ajax.reload();
    }, 5000);
});