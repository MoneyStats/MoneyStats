$(document).ready(function () {

    /*--------------------------------------------------------------------------
    *  Variables to handle the possible exception during signUp process
    *--------------------------------------------------------------------------*/
    const WALLET_NOT_FOUND = "WALLET_NOT_FOUND";
    const STATEMENT_NOT_FOUND = "STATEMENT_NOT_FOUND";
    const INVALID_WALLET_DTO = "INVALID_WALLET_DTO";
    const TOKEN_REQUIRED = "TOKEN_REQUIRED";
    const INVALID_TOKEN_DTO = "INVALID_TOKEN_DTO";
    const CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
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
        console.log("User Logged with accessToken {}, ", authCredentialDTO.firstName, authCredentialDTO.lastName, " username -> ", authCredentialDTO.username);
        $('#options').text(`Opzioni - ${authCredentialDTO.username}`);
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
            title: '<span style="color:#2D2C2C">Sessione Scaduta, reinderizzazione...</span>'
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
    // Modal Get Wallet List Homepage
    //-------------------------------------------------------------
    function getWallet(){
        $.ajax({
            type: "GET",
            url: "/wallet/listMobile",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
              Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (resume){
                const listWallet = $('#listWallet');
                for (let i = resume.walletEntities.length - 1; i >= 0; i--) {
                var defaultValue = 0.00;
                let img = '';
                let color = '';
                switch (resume.walletEntities[i].category.name){
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
                if (resume.statementEntities[i].value != undefined){
                    defaultValue = resume.statementEntities[i].value;
                }
                $(`<!-- card block -->
                <div class="card-block ${color} mb-2">
                    <div class="card-main">
                        <div class="card-button dropdown">
                            <button type="button" class="btn btn-link btn-icon" data-bs-toggle="dropdown">
                                <ion-icon name="ellipsis-horizontal"></ion-icon>
                            </button>
                            <div class="dropdown-menu dropdown-menu-end">
                                <a class="dropdown-item" href="javacript:;">
                                    <ion-icon name="pencil-outline"></ion-icon>Edit
                                </a>
                                <a class="dropdown-item btn-elimina-wallet" href="javacript:;">
                                    <ion-icon name="close-outline"></ion-icon>Remove
                                </a>
                            </div>
                        </div>
                        <div class="balance">
                            <span class="label">BALANCE</span>
                            <h1 class="title">€ ${defaultValue}</h1>
                        </div>
                        <div class="in">
                            <div class="card-number">
                                <span class="label">Wallet Name</span>
                                ${resume.walletEntities[i].name}
                            </div>
                            <div class="bottom">
                                <div class="card-expiry">
                                    <span class="label">Category</span>
                                    ${resume.walletEntities[i].category.name}
                                </div>
                                <div class="card-ccv">
                                    <span class="label">User</span>
                                    ${resume.statementEntities[i].user.firstName} ${resume.statementEntities[i].user.lastName}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- * card block -->`).hide().appendTo(listWallet).fadeIn(i * 20);
            }
        }
        })
    }
    //-------------------------------------------------------------
    // END Modal Get Wallet List Homepage
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Modal Get Wallet List Homepage (Edit Wallet)
    //-------------------------------------------------------------
    //Modifica un ristorante dalla lista principale
    /*let editMode = false;
    let idModifica = -1;

    function editWallet(wallet) {
        $.ajax({
            type: "PUT",
            url: `/user/ristorantiuser`,
            data: JSON.stringify(ristorante),
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {
                editMode = false;
                idModifica = -1;
            },
            error: function (error) {
                 alert("Problema nella modifica");
             }
        });
    }

    $('#listaRistorantiUser').on('click', '.btn-modifica-risto', function () {
        editMode = true;
        const id = +$(this).attr('data-id');
        idModifica = id;
        $.get(`/user/ristorantiuser/${id}`, function (modifica) {
            let img = '';
            if (modifica.immagini === null) {
                img = '../logos/logo.png';
            } else {
                img = '../upload/' + modifica.immagini;
            }
        $('#modificaRistoranteLogo').attr('src', img);
            $('#ragionesociale').val(modifica.ragionesociale);
            $('#piva').val(modifica.piva);
            $('#cittaRistorante').val(modifica.citta);
            $('#regioneRistorante').val(modifica.regione);
            $('#viaRistorante').val(modifica.via);
            $('#ncivico').val(modifica.ncivico);
            $('#modificaRistoranteTitle').text('Modifica ' + modifica.ragionesociale);
            $('#modificaRistorante').text('Modifica ' + modifica.ragionesociale);
            $('#title').text('Modifica ' + modifica.ragionesociale);
        });
    });
    $('#modificaRistorante').click(function () {
        const ristorante = {
            ragionesociale: $('#ragionesociale').val(),
            piva: $('#piva').val(),
            citta: $('#cittaRistorante').val(),
            regione: $('#regioneRistorante').val(),
            via: $('#viaRistorante').val(),
            ncivico: $('#ncivico').val()
        }
        console.log(ristorante);
        if (editMode) {
            Swal.fire({
                icon: 'question',
                title: 'Vuoi salvare la Modifica di ' + ristorante.ragionesociale + '?',
                showDenyButton: true,
                showCancelButton: true,
                confirmButtonText: `Salva`,
                denyButtonText: `Non Salvare`,
            }).then((result) => {
                if (result.isConfirmed) {
                    Swal.fire('Salvato!', '', 'success')
                    ristorante.id = idModifica;
                    modificaRistorante(ristorante);
                    setTimeout(function () {
                        window.location.href = 'ristorantiUser.html';
                    }, 2000);
                } else if (result.isDenied) {
                    Swal.fire('Modifiche non salvate', '', 'info')
                }
            })
        }
    })*/
    //-------------------------------------------------------------
    // END Modal Get Wallet List Homepage (Edit Wallet)
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Modal Get Wallet List Homepage (Delete Wallet)
    //-------------------------------------------------------------
    function deleteWallet(id) {
        let idPagina = $(`#riga-${id}`);
        $.ajax({
            type: "DELETE",
            url: `/wallet/delete/${id}`,
            headers: {
              Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (response){
                swalWithBootstrapButtons.fire(
                    '<span style="color:#2D2C2C">Cancelled!</span>',
                    'Your walles was successfuly deleted.',
                    'success'
                )
                idPagina.slideUp(300, function () {
                    idPagina.remove();
                })
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === STATEMENT_NOT_FOUND){
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Error, Delete process aborted</span>',
                        text: 'Try again.'
                    })
                }
                if (responseDTO === WALLET_NOT_FOUND){
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Error, Delete process aborted</span>',
                        text: 'Try again.'
                    })
                }
            }
        });
    }

    $('#listWallet').on('click', '.btn-elimina-wallet', function () {
        const id = $(this).attr('data-id');
        const swalWithBootstrapButtons = Swal.mixin({
            customClass: {
                confirmButton: 'btn btn-danger',
                cancelButton: 'btn btn-primary mx-2'
            },
            buttonsStyling: false
        })
        swalWithBootstrapButtons.fire({
            title: '<span style="color:#2D2C2C">Are you sure?</span>',
            text: "You can't go back!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Delete',
            cancelButtonText: 'Exit',
            reverseButtons: true
        }).then((result) => {
            if (result.isConfirmed) {
                deleteWallet(id);
            } else if (
                result.dismiss === Swal.DismissReason.cancel
            ) {
                swalWithBootstrapButtons.fire(
                    '<span style="color:#2D2C2C">Exit</span>',
                    'Your wallet is safe',
                    'error'
                )
            }
        })
    });
    //-------------------------------------------------------------
    // END Modal Get Wallet List Homepage (Delete Wallet)
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Custom Button Swal
    //-------------------------------------------------------------
    const swalWithBootstrapButtons = Swal.mixin({
        customClass: {
          confirmButton: 'btn btn-success',
          cancelButton: 'btn btn-danger',
          denyButton: 'btn btn-danger'
        },
        buttonsStyling: false
      })
    //-------------------------------------------------------------
    // END Custom Button Swal
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Modal Add Wallet Homepage
    //-------------------------------------------------------------
        $('#aggiungiWallet').click(function () {
            const wallet = {
                name: $('#walletName').val(),
                categoryId: $('#catOptionhtml').val(),
            }
            Swal.fire({
                icon: 'question',
                title: `<span style="color:#2D2C2C">Confirm save of ${wallet.name}?</span>`,
                showDenyButton: true,
                confirmButtonText: `Save`,
                denyButtonText: `Don't Save`,
              }).then((result) => {
                if (result.isConfirmed) {
                    addWallet(wallet);
                } else if (result.isDenied) {
                  swalWithBootstrapButtons.fire(`Wallet don't added`, '', 'info')
                }
              })
            

        $('#walletName').val('');
    })

    function addWallet(wallet) {
        $.ajax({
            type: "POST",
            url: `/wallet/addWallet/${wallet.categoryId}`,
            data: JSON.stringify(wallet),
            contentType: 'application/json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (response) {
                Swal.fire('<span style="color:#2D2C2C">Saved!</span>', '', 'success')
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_TOKEN_DTO){
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Operation Aborted</span>',
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === TOKEN_REQUIRED){
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Operation Aborted!</span>',
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === INVALID_WALLET_DTO){
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Operation Aborted!</span>',
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === CATEGORY_NOT_FOUND){
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Operation Aborted!</span>',
                        text: 'Error during the process.'
                    })
                }
            }
        });
    }
    //-------------------------------------------------------------
    // END Modal Add Wallet Homepage
    //-------------------------------------------------------------
});