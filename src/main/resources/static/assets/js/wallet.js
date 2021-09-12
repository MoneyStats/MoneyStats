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

    //-------------------------------------------------------------
    // Modal Get Wallet List Homepage
    //-------------------------------------------------------------
    getWallet();
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
            for (let i = resume.length - 1; i >= 0; i--) {
                $(`<tr id='riga-${resume[i].id}'>
                <td>${resume[i].name}</td>
                <td>${resume[i].category.name}</td>
                <td>
                    <div class="btn-group roundedCorner" role="group">
                        <button id="btnGroupDrop1" type="button" class="btn btn-outline-primary roundedCorner dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">Opzioni</button>
                            <ul class="dropdown-menu" aria-labelledby="btnGroupDrop1">
                                <li><a class="dropdown-item btn-modifica-risto" data-bs-toggle="modal" data-bs-target="#modifica" data-id='${resume[i].id}'>Modifica</a></li>
                                <li><a class="dropdown-item btn-elimina-wallet" data-id='${resume[i].id}'>Elimina</a></li>
                            </ul>
                    </div>
                </td>
            </tr>`).hide().appendTo(listWallet).fadeIn(i * 20);
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
                    'Cancelled!',
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
                        title: "Error, Delete process aborted",
                        text: 'Try again.'
                    })
                }
                if (responseDTO === WALLET_NOT_FOUND){
                    Swal.fire({
                        icon: 'error',
                        title: "Error, Delete process aborted",
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
            title: 'Are you sure?',
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
                    'Exit',
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
                title: `Confirm save of ${wallet.name}?`,
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
            url: `/wallet/addWallet`,
            data: JSON.stringify(wallet),
            contentType: 'application/json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (response) {
                Swal.fire('Saved!', '', 'success')
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_TOKEN_DTO){
                    Swal.fire({
                        icon: 'error',
                        title: "Operation Aborted!",
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === TOKEN_REQUIRED){
                    Swal.fire({
                        icon: 'error',
                        title: "Operation Aborted!",
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === INVALID_WALLET_DTO){
                    Swal.fire({
                        icon: 'error',
                        title: "Operation Aborted!",
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === CATEGORY_NOT_FOUND){
                    Swal.fire({
                        icon: 'error',
                        title: "Operation Aborted!",
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