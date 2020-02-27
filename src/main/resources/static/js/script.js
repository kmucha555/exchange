$(document).ready(function () {
    let publicationDate;

    $.fn.dataTable.ext.errMode = (settings, techNote, message) => {
        // clearInterval(updateInterval);
        $("#lastUpdate").html(`<h3 class="text-danger">System temporary unavailable</h3><h6 class="text-danger">${message}</h6>`);
        $("#wallet").find('a').hide();
        $("#currencies").find('a').hide();
    };

    const currencies = $('#currencies')
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
                    $("#lastUpdate").html(`Last update: ${publicationDate.toLocaleDateString()} ${publicationDate.toLocaleTimeString()}`);
                    json.items.forEach(element =>
                        returnData.push(
                            {
                                'code': element.code,
                                'unit': element.unit,
                                'sellPrice': element.sellPrice.toFixed(4),
                                'action': `<a class="btn-sm btn-warning" href="/transaction/buy/${element.currencyRateId}">Buy</a>`
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
                                'amount': element.amount.toFixed(0),
                                'purchasePrice': element.purchasePrice.toFixed(4),
                                'value': (element.amount * element.purchasePrice / element.unit).toFixed(2),
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

    const updateInterval = setInterval(() => {
        currencies.ajax.reload();
        wallet.ajax.reload();
    }, 5000);
});