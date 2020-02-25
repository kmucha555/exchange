$(document).ready(function () {
    const url = window.location.href + '/currencies';
    let publicationDate;
    const currencies = $('#currencies').DataTable({
        "order": [[0, "asc"]],
        "paging": false,
        "ordering": true,
        "info": false,
        "searching": false,
        "scrollX": false,
        "scrollY": false,
        "dom": '<"toolbar">frtip',
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
                            'purchasePrice': element.purchasePrice
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
            {data: 'purchasePrice'}
        ]
    });
    setInterval(() => currencies.ajax.reload(), 5000);
});
