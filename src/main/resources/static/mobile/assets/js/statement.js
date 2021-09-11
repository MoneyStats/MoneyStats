$(document).ready(function () {

  var COUNT_ERROR = 0;

    /*--------------------------------------------------------------------------
    * Get all wallet for the statement
    *--------------------------------------------------------------------------*/
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
          const listWallet = $('#listWallet');
          for (let i = 0; i < resume.length; i++) {
              document.cookie = "id"+i+" = " + resume[i].id;
              $(`<div class="form-group basic animated">
              <div class="input-group input-wrapper">
                  <label class="label" for="value${resume[i].id}">${resume[i].name} (${resume[i].category.name})</label>
                  <span class="input-group-text" id="basic-addona1">€</span>
                  <input type="text" class="form-control" id="value${resume[i].id}" placeholder="${resume[i].name}">
                  <input type='hidden' id="wallet${resume[i].id}" value="${resume[i].id}">
                  <i class="clear-input">
                      <ion-icon name="close-circle"></ion-icon>
                  </i>
              </div>
              <div class="input-info">Please enter amount for ${resume[i].name}</div>
          </div>`).hide().appendTo(listWallet).fadeIn(i * 20);
          }
        },
        error: function () {
          const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1000,
            timerProgressBar: true,
          })
          Toast.fire({
            icon: 'error',
            title: '<span style="color:#2D2C2C">Error loading...</span>'
          })
          setTimeout(function () {
            window.location.href = "index.html";
          }, 1000);
        }
      })
    }
    /*--------------------------------------------------------------------------
    * END Get all wallet for the statement
    *--------------------------------------------------------------------------*/
      getWallet();
      var cookiearray = [];
      var valueCookie;

    /*--------------------------------------------------------------------------
    * add statement for each wallet
    *--------------------------------------------------------------------------*/
      $('#aggiungiStatement').click(function () {
        var cookie = document.cookie;
        cookiearray = cookie.split(';');
        for (let i = 0; i < cookiearray.length; i++){
          valueCookie = cookiearray[i].split('=')[1];
          $(`#value${valueCookie}`).prop('required', true);
        }
        Swal.fire({
            title: '<span style="color:#2D2C2C">Do you want to safe the current Statement?</span>',
            showDenyButton: true,
            confirmButtonText: `Safe`,
            denyButtonText: `Don't Safe`,
            confirmButtonColor: '#3085d6',
            denyButtonColor: '#d33',
            icon: 'question',
          }).then((result) => {
            if (result.isConfirmed) {
                for (let i = 0; i < cookiearray.length; i++){
                    valueCookie = cookiearray[i].split('=')[1];
                    var statement = {
                        value: $(`#value${valueCookie}`).val(),
                        date: $('#date').val(),
                        walletId: $(`#wallet${valueCookie}`).val(),
                    }
                    addStatement(statement);

                    $(`#value${valueCookie}`).val('');
                    $(`#wallet${valueCookie}`).val('');
                }
                if (COUNT_ERROR === 0){
                  $('#date').val('');
                  document.cookie.split(";").forEach(function(c) { document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); });
                  Swal.fire({
                    title: '<span style="color:#2D2C2C">Saved!</span>',
                    text: "New Statement?",
                    showDenyButton: true,
                    confirmButtonText: `Yes`,
                    denyButtonText: `No, go to Homepage`,
                    confirmButtonColor: '#3085d6',
                    denyButtonColor: '#d33',
                    icon: 'success'
                  }).then((result) => {
                    if (result.isConfirmed) {
                    } else if (result.isDenied) {
                      const Toast = Swal.mixin({
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 1000,
                        timerProgressBar: true,
                      })
                      
                      Toast.fire({
                        icon: 'success',
                        title: '<span style="color:#2D2C2C">Render Homepage...</span>'
                      })
                      setTimeout(function () {
                        window.location.href = "index.html";
                      }, 1000);
                    }
                    // Needs COUNT_ERROR to check the statement process
                    COUNT_ERROR = 0;
                  })
                }
            } else if (result.isDenied) {
                  Swal.fire(
                    '<span style="color:#2D2C2C">Aborted</span>',
                    "Statement don'added",
                    'error'
                    )
                  }
          })        
  })

    function addStatement(statement) {
      $.ajax({
        type: "POST",
        url: "/statement/addStatement",
        data: JSON.stringify(statement),
        contentType: 'application/json',
        headers: {
          Authorization: sessionStorage.getItem('accessToken')
        },
        success: function (statementReportDTO) {
        },
        error: function () {
          const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1000,
            timerProgressBar: true,
          })
          
          Toast.fire({
            icon: 'error',
            title: '<span style="color:#2D2C2C">Error Add Statement...</span>'
          })
          COUNT_ERROR++;
        }
      });
    }
    /*--------------------------------------------------------------------------
    * END add Statement
    *--------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------
    * Reset Cookies
    *--------------------------------------------------------------------------*/
    $('.resetCookies').on('click', function resetCookies(){
      document.cookie.split(";").forEach(function(c) { document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); });
    })
});

