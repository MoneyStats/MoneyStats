$(document).ready(function () {
    /*--------------------------------------------------------------------------
    *  Variables to handle the possible exception during signUp process
    *--------------------------------------------------------------------------*/
  const LOGIN_REQUIRED = "LOGIN_REQUIRED";

  //-------------------------------------------------------------
  // Check if session is validated with a user
  //-------------------------------------------------------------
  isValidated();
  function isValidated (){
    const token = sessionStorage.getItem('accessToken');
    if (token === null) {
      window.location.href='app-login.html';
    } 
  //-------------------------------------------------------------
  // Check if session is validated with a user
  //-------------------------------------------------------------
  $.ajax({
    type: "GET",
    url: "/check_login",
    contentType: 'application/json',
    dataType: 'json',
    headers: {
      Authorization: sessionStorage.getItem('accessToken')
    },
    success: function (authCredentialDTO){
    },
    error: function (authErrorResponseDTO) {
      var responseDTO = authErrorResponseDTO.responseJSON.error;
      if (responseDTO === LOGIN_REQUIRED){
        const Toast = Swal.mixin({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 1500,
          timerProgressBar: true,
        })
        Toast.fire({
          icon: 'error',
          title: '<span style="color:#2D2C2C">Session Expired, redirect...</span>'
        })
        setTimeout(function () {
          window.location.href = "app-login.html";
        }, 1500);
      }
    }
  })
  getWallet();
}
//-------------------------------------------------------------
  // END Check if session is validated with a user
  //-------------------------------------------------------------

  //-------------------------------------------------------------
  // Get List wallet
  //-------------------------------------------------------------
    var tot = 0;
    var totold = 0;
    var dataold;
    // WALLET NELLA CARD
    function getWallet(){
      $.ajax({
        type: "GET",
        url: "/wallet/list",
        contentType: 'application/json',
        dataType: 'json',
        headers: {
          Authorization: sessionStorage.getItem('accessToken')
        },
        success: function (resume){
          $('#title').text('MoneyStats - Total On ' + localStorage.getItem('date'));
          $('.pageTitle').text('Statement On ' + localStorage.getItem('date'));
         const listWallet = $('#capitalewallet');
         for (let i = 0; i < resume.length; i++) {
           let img = '';
                  let color = '';
                  switch (resume[i].category.name){
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
                $(`<div class="col-xl-3 col-sm-6">
                <div class="card card-stats mb-4 mb-xl-0">
                  <div class="card-body">
                  <div class="row">
                      <div class="col">
                      <h5 class="card-title text-uppercase text-muted mb-0">${resume[i].name} (${resume[i].category.name})</h5>
                      <span class="h2 font-weight-bold mb-0" id="tot${resume[i].id}">€ 0.0</span>
                      </div>
                      <div class="col-auto">
                      <div class="icon icon-shape ${color} text-white rounded-circle shadow">
                        <i class="${img}"></i>
                          </div>
                          </div>
                    </div>
                    <p class="mt-3 mb-0 text-muted text-sm">
                    <span class="performancesingle${i}"></span>
                    <span class="text-nowrap sincetot">Since last month</span>
                      </p>
                      </div>
                </div>
                </div>`).hide().appendTo(listWallet).fadeIn(i * 20);
              }
            }
      })
            
    }
    //-------------------------------------------------------------
  // END Get List wallet
  //-------------------------------------------------------------
    
  //-------------------------------------------------------------
  // Get List statement
  //-------------------------------------------------------------
    getStatement();
    function getStatement(){
      $.ajax({
        type: "GET",
        url: `/statement/listStatementDate/${localStorage.getItem('date')}`,
        contentType: 'application/json',
        dataType: 'json',
        headers: {
          Authorization: sessionStorage.getItem('accessToken')
        },
        success: function (statement){
        for (let i = 0; i < statement.length; i++) {
          var value = statement[i].value;
          tot += value;
          $(`#tot${statement[i].wallet.id}`).text('€ ' + statement[i].value.toFixed(2)).val(statement[i].value);
        }
        $('#totale').text("€ " + tot.toFixed(2)).val(tot);
        getDate();
      }
      })
    }
    //-------------------------------------------------------------
  // END Get List statement
  //-------------------------------------------------------------
    
  //-------------------------------------------------------------
  // Get List date(date stored on localStorage)
  //-------------------------------------------------------------
    function getDate() {
      var listDate = localStorage.getItem('listDate');
      var date = listDate.split(",");
        for (let i = 0; i < date.length; i++) {
          if (date[i] === localStorage.getItem('date')){
            dataold = date[i - 1];
            $('.sincetot').text("Since " + dataold);
          }
        }
        getOld(dataold);
    }
    //-------------------------------------------------------------
  // END Get List date(date stored on localStorage)
  //-------------------------------------------------------------
    
  //-------------------------------------------------------------
  // Get date statement
  //-------------------------------------------------------------
    function getOld(dataold) {
      $.ajax({
        type: "GET",
        url: `/statement/listStatementDate/${dataold}`,
        contentType: 'application/json',
        dataType: 'json',
        headers: {
          Authorization: sessionStorage.getItem('accessToken')
        },
        success: function (performance){
          for (let i = 0; i < performance.length; i++) {
            var value1 = performance[i].value;
            totold += value1;
            // CALCOLO SINGOLI VALORI
            var valueOld = performance[i].value;
            var valueNew = $(`#tot${performance[i].wallet.id}`).val();
            var totValue = ((valueNew - valueOld) / valueOld) * 100;
            if (totValue > 0){
              $(`<span class="text-success mr-2"><i class="fa fa-arrow-up"></i> ${totValue.toFixed(2)}%</span>`).appendTo(`.performancesingle${i}`)
            } else if (totValue === 0){
              $(`<span class="text-warning mr-2"><i class="fa fa-arrow-down"></i> ${totValue.toFixed(2)}%</span>`).appendTo(`.performancesingle${i}`)
            } else {
              $(`<span class="text-danger mr-2"><i class="fa fa-arrow-down"></i> ${totValue.toFixed(2)}%</span>`).appendTo(`.performancesingle${i}`)
            }
          }
          var tot = $('#totale').val();
          var pil = tot - totold;
          var performance = ((tot - totold) / totold) * 100;
          if (performance > 0){
            $(`<span class="text-success mr-2"><i class="fa fa-arrow-up"></i> ${performance.toFixed(2)}%</span>`).appendTo('.performancetot')
            $('#pil').text("€ " + pil.toFixed(2)).addClass('text-success');
          } else if (performance === 0){
            $(`<span class="text-warning mr-2"><i class="fa fa-arrow-down"></i> ${performance.toFixed(2)}%</span>`).appendTo('.performancetot')
            $('#pil').text("€ " + pil.toFixed(2)).addClass('text-warning');
          } else {
            $(`<span class="text-danger mr-2"><i class="fa fa-arrow-down"></i> ${performance.toFixed(2)}%</span>`).appendTo('.performancetot')
            $('#pil').text("€ " + pil.toFixed(2)).addClass('text-danger');
          }
        }
      })
    }    
});