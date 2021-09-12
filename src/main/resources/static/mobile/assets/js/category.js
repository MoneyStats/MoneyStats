$(document).ready(function () {
    //-------------------------------------------------------------
    // Modal Get Category List Homepage
    //-------------------------------------------------------------
    getCategory();
    function getCategory(){
        $.ajax({
            type: "GET",
            url: "/category/list",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
              Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (resume){
            const listCategory = $('#listCategory');
            const optionCategory = $('#catOptionhtml');
            for (let i = resume.length - 1; i >= 0; i--) {
                let img = '';
                let color = '';
                switch (resume[i].name){
                    case 'Contanti':
                        img = 'fas fa-money-bill-wave';
                        color = 'bg-success';
                        break;
                    case 'Conto Corrente':
                        img = 'fas fa-landmark';
                        color = 'bg-warning';
                        break;
                    case 'Carte di Credito':
                        img = 'far fa-credit-card';
                        color = 'bg-danger';
                        break;
                    case 'Carte di Debito':
                        img = 'fas fa-credit-card';
                        color = 'bg-dark';
                        break;
                    case 'Cupon':
                        img = 'fas fa-receipt';
                        color = 'bg-info';
                        break;
                    case 'Risparmi':
                        img = 'fas fa-piggy-bank';
                        color = 'bg-success';
                        break;
                    case 'Cash Elettronico':
                        img = 'fas fa-money-bill';
                        color = 'bg-primary';
                        break;
                    case 'Investimenti':
                        img = 'fas fa-chart-line';
                        color = 'bg-primary';
                        break;
                    case 'Mutui':
                        img = 'fas fa-balance-scale-left';
                        color = 'bg-danger';
                        break;
                    case 'Assicurazioni':
                        img = 'fas fa-file-invoice-dollar';
                        color = 'bg-warning';
                        break;
                    case 'Assegni':
                        img = 'fas fa-money-check-alt';
                        color = 'bg-info';
                        break;
                    case 'Altro':
                        img = 'fas fa-hand-holding-usd';
                        color = 'bg-secondary';
                        break;
                    default:
                        img = 'fas fa-chart-bar';
                        color = 'bg-dark';
                        break;
                }
                $(`<tr id='riga-${resume[i].id}'>
                <td><div class="col-auto">
                <div class="icon icon-shape ${color} text-white rounded-circle shadow">
                <i class="${img}"></i>
                    </div>
                    </div></td>
                <td class='space' style='margin-left: 0px;'>${resume[i].name}</td>
                </tr>`).hide().appendTo(listCategory).fadeIn(i * 20);
                $(`<option id='walletSelect' class="roundedCorner" value="${resume[i].id}">${resume[i].name}</option>`).hide().appendTo(optionCategory).fadeIn(i * 20);
                }
            }
        })
    }
    //-------------------------------------------------------------
    // End Modal Get Category List Homepage
    //-------------------------------------------------------------
});