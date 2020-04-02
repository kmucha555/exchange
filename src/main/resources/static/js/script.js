$(document).ready(function () {
    let publicationDate;

    $.fn.dataTable.ext.errMode = () => {
        $("#lastUpdate").html(`<h3 class="text-danger">System temporary unavailable</h3>`);
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
                    $('#lastUpdate').html(`Last update: ${publicationDate.toLocaleDateString()} ${publicationDate.toLocaleTimeString()}`);
                    json.items.forEach(element => {
                        const sellPrice = parseFloat(element.sellPrice.toFixed(4));
                        const unit = parseInt(element.unit);
                        const availableFunds = parseFloat($('#funds').text());
                        returnData.push(
                            {
                                'code': element.code,
                                'unit': unit,
                                'sellPrice': sellPrice,
                                'action': availableFunds >= sellPrice ?
                                    `<a class="btn-sm btn-warning" href="/transaction/buy/${element.currencyRateId}">Buy</a>`
                                    : ``
                            }
                        )
                    });
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
                    json.forEach(element => {
                        const amount = parseInt(element.amount.toFixed(0));
                        const purchasePrice = parseFloat(element.purchasePrice.toFixed(4));
                        const unit = parseInt(element.unit);
                        returnData.push(
                            {
                                'code': element.code,
                                'amount': amount,
                                'purchasePrice': purchasePrice,
                                'value': (amount * purchasePrice / unit).toFixed(2),
                                'action': amount === 0 ? `` : `<a class="btn-sm btn-danger" href="/transaction/sell/${element.currencyRateId}">Sell</a>`
                            }
                        )
                    });
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