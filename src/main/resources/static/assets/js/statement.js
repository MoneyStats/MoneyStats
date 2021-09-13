$(document).ready(function () {

    const LOGIN_REQUIRED = "LOGIN_REQUIRED";
    var COUNT_ERROR = 0;

    //-------------------------------------------------------------
    // Check if session is validated with a user
    //-------------------------------------------------------------
    isValidated();

    function isValidated() {
        const token = sessionStorage.getItem('accessToken');
        if (token === null) {
            window.location.href = 'loginPage.html';
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
            success: function (authCredentialDTO) {
                console.log("User Logged with accessToken {}, ", authCredentialDTO.firstName, authCredentialDTO.lastName, " username -> ", authCredentialDTO.username);
                $('#options').text(`Opzioni - ${authCredentialDTO.username}`);
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.error;
                if (responseDTO === LOGIN_REQUIRED) {
                    const Toast = Swal.mixin({
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 1500,
                        timerProgressBar: true,
                    })
                    Toast.fire({
                        icon: 'error',
                        title: 'Sessione Scaduta, reinderizzazione...'
                    })
                    setTimeout(function () {
                        window.location.href = "loginPage.html";
                    }, 1500);
                }
            }
        })
    }

    function getWallet() {
        $.ajax({
            type: "GET",
            url: "/wallet/list",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (resume) {
                const listWallet = $('#listWallet');
                for (let i = 0; i < resume.length; i++) {
                    document.cookie = "id" + i + " = " + resume[i].id;
                    let img = '';
                    let color = '';
                    switch (resume[i].category.name) {
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
                    $(`<div class="col-xl-3 col-sm-6" style="margin-bottom: 30px;">
              <div class="card card-stats mb-4 mb-xl-0">
                <div class="card-body">
                <div class="row">
                    <div class="col">
                    <h5 class="card-title text-uppercase text-muted mb-0">${resume[i].name} (${resume[i].category.name})</h5>
                    <hr>
                    <div class='form-floating mb-3'> 
                    <input style="background-color: rgba(255, 255, 255, 0.7);" type='number' id="value${resume[i].id}" class='form-control roundedCorner mx-auto' placeholder="Inserire £GBP..." required>
                    <label for='value'>Inserire £GBP</label>
                </div>
                    <input type='hidden' id="wallet${resume[i].id}" value="${resume[i].id}">
                    </div>
                    <div class="col-auto">
                    <div class="icon icon-shape ${color} text-white rounded-circle shadow">
                      <i class="${img}"></i>
                        </div>
                        </div>
                  </div>
                  <p class="mt-3 mb-0 text-muted text-sm">
                  <span class="text-nowrap sincetot">Inserire Valori</span>
                    </p>
                    </div>
              `).hide().appendTo(listWallet).fadeIn(i * 20);
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
                    title: 'Error loading...'
                })
                setTimeout(function () {
                    window.location.href = "homepage.html";
                }, 1000);
            }
        })
    }

    getWallet();
    var cookiearray = [];
    var valueCookie;
    $('#aggiungiStatement').click(function () {
        var cookie = document.cookie;
        cookiearray = cookie.split(';');
        for (let i = 0; i < cookiearray.length; i++) {
            valueCookie = cookiearray[i].split('=')[1];
            $(`#value${valueCookie}`).prop('required', true);
        }
        Swal.fire({
            title: 'Do you want to safe the current Statement?',
            showDenyButton: true,
            confirmButtonText: `Safe`,
            denyButtonText: `Don't Safe`,
            confirmButtonColor: '#3085d6',
            denyButtonColor: '#d33',
            icon: 'question',
        }).then((result) => {
            if (result.isConfirmed) {
                for (let i = 0; i < cookiearray.length; i++) {
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
                if (COUNT_ERROR === 0) {
                    $('#date').val('');
                    document.cookie.split(";").forEach(function (c) {
                        document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
                    });
                    Swal.fire({
                        title: 'Saved!',
                        text: "New Statement?",
                        showDenyButton: true,
                        confirmButtonText: `Yes`,
                        denyButtonText: `No, go to Homepage`,
                        confirmButtonColor: '#3085d6',
                        denyButtonColor: '#d33',
                        icon: 'success'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.location.href = "statement.html";
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
                                title: 'Render Homepage...'
                            })
                            setTimeout(function () {
                                window.location.href = "homepage.html";
                            }, 1000);
                        }
                        COUNT_ERROR = 0;
                    })
                }
            } else if (result.isDenied) {
                Swal.fire(
                    'Aborted',
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
                    title: 'Error Add Statement...'
                })
                COUNT_ERROR++;
            }
        });
    }

    $('.resetCookies').on('click', function resetCookies() {
        document.cookie.split(";").forEach(function (c) {
            document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
        });
    })

    /*--------------------------------------------------------------------------
    *  Switch Mobile Mode
    *--------------------------------------------------------------------------*/
    $('#SwitchMobile').click(function () {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1000,
            timerProgressBar: true,
        })
        Toast.fire({
            icon: 'info',
            title: "<span style='color:#2D2C2C'>Reinderizzazione...</span>",
        })
        setTimeout(function () {
            window.location.href = "mobile/index.html";
        }, 1000);
    });
    /*--------------------------------------------------------------------------
     *  Switch Mobile Mode
     *--------------------------------------------------------------------------*/

});

