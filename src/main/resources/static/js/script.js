$(document).ready(() => {
    const fetchProgress = () => {
        let retries = 0;
        const handleError = (xhr, status, err) => {
            console.log(xhr);
            console.log(status);
            console.log(err);
        };

        const interval = setInterval(() => {
            $.ajax({
                url: window.location.href + '/fetch-progress',
                method: 'get',
                dataType: 'json'
            }).done(result => {
                console.log(result);
                if (result >= 0) {
                    if (result >= 100) {
                        clearInterval(interval);
                        $('#fetch-progress').addClass('d-none');
                    } else {
                        $('#fetch-progress').removeClass('d-none');
                        $('#dynamic')
                            .css('width', result + '%')
                            .attr('aria-valuenow', result)
                            .text(result + '% Complete');
                    }
                } else {
                    retries++;
                }
                if (retries === 3) {
                    clearInterval(interval);
                }
            }).fail((xhr, status, err) => handleError(xhr, status, err));
        }, 2000);
    };
    fetchProgress();
});
