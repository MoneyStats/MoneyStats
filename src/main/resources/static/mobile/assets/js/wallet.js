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

    function isValidated() {
        const token = sessionStorage.getItem('accessToken');
        if (token === null) {
            window.location.href = 'app-login.html';
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
                $('#options').text(`Oprions - ${authCredentialDTO.username}`);
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
                        title: '<span style="color:#2D2C2C">Session Expired, reidirect...</span>'
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
    function getWallet() {
        $.ajax({
            type: "GET",
            url: "/wallet/listMobile",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (resume) {
                const listWallet = $('#listWallet');
                for (let i = resume.walletEntities.length - 1; i >= 0; i--) {
                    var defaultValue = 0.00;
                    let img = '';
                    let color = '';
                    switch (resume.walletEntities[i].category.name) {
                        case 'Cash':
                            img = 'fas fa-money-bill-wave';
                            color = 'bg-success';
                            break;
                        case 'Bank Account':
                            img = 'fas fa-landmark';
                            color = 'bg-warning';
                            break;
                        case 'Credit Card':
                            img = 'far fa-credit-card';
                            color = 'bg-danger';
                            break;
                        case 'Debit Card':
                            img = 'fas fa-credit-card';
                            color = 'bg-dark';
                            break;
                        case 'Coupon':
                            img = 'fas fa-receipt';
                            color = 'bg-info';
                            break;
                        case 'Safe':
                            img = 'fas fa-piggy-bank';
                            color = 'bg-success';
                            break;
                        case 'Cash Electronic':
                            img = 'fas fa-money-bill';
                            color = 'bg-primary';
                            break;
                        case 'Investments':
                            img = 'fas fa-chart-line';
                            color = 'bg-primary';
                            break;
                        case 'Recurrence':
                            img = 'fas fa-balance-scale-left';
                            color = 'bg-danger';
                            break;
                        case 'Assicurazioni':
                            img = 'fas fa-file-invoice-dollar';
                            color = 'bg-warning';
                            break;
                        case 'Check':
                            img = 'fas fa-money-check-alt';
                            color = 'bg-info';
                            break;
                        case 'Others':
                            img = 'fas fa-hand-holding-usd';
                            color = 'bg-secondary';
                            break;
                        default:
                            img = 'fas fa-chart-bar';
                            color = 'bg-dark';
                            break;
                    }
                    if (resume.statementEntities[i].value != undefined) {
                        defaultValue = resume.statementEntities[i].value;
                    }
                    $(`<!-- card block -->
                <div class="card-block ${color} mb-2" id='riga-${resume.walletEntities[i].id}'>
                    <div class="card-main">
                        <div class="card-button dropdown">
                            <button type="button" class="btn btn-link btn-icon" data-bs-toggle="dropdown">
                                <ion-icon name="ellipsis-horizontal"></ion-icon>
                            </button>
                            <div class="dropdown-menu dropdown-menu-end">
                                <a class="dropdown-item btn-modifica-wallet" data-bs-toggle="modal" data-bs-target="#editWalletActionSheet" data-id='${resume.walletEntities[i].id}'>
                                    <ion-icon name="pencil-outline"></ion-icon>Edit
                                </a>
                                <a class="dropdown-item btn-elimina-wallet" data-id='${resume.walletEntities[i].id}'">
                                    <ion-icon name="close-outline"></ion-icon>Remove
                                </a>
                            </div>
                            <div class="col-auto" style="padding-top: 100px;">
                      <div class="icon icon-shape ${color} text-white rounded-circle shadow">
                        <i class="${img}"></i>
                          </div>
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
    let editMode = false;
    let idModifica = -1;

    function editWallet(walletEdit) {
        $.ajax({
            type: "PUT",
            url: `/wallet/editWallet`,
            data: JSON.stringify(walletEdit),
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (response) {
                editMode = false;
                idModifica = -1;
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1500,
                    timerProgressBar: true,
                })
                Toast.fire({
                    icon: 'success',
                    title: '<span style="color:#2D2C2C">Edited, Refresh...</span>'
                })
                setTimeout(function () {
                    window.location.href = 'app-wallet.html';
                }, 1000);
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: '<span style="color:#2D2C2C">Error, Delete process aborted</span>',
                    text: 'Try again.'
                })
            }
        });
    }

    //------------------------------------------------------------------------------------
    // On click on the wallet list of button edit, show wallet name and list category
    //------------------------------------------------------------------------------------
    $('#listWallet').on('click', '.btn-modifica-wallet', function () {
        editMode = true;
        const id = +$(this).attr('data-id');
        idModifica = id;
        const optionCategory = $('#catOptionhtmlEdit');
        $.ajax({
            type: "GET",
            url: `/wallet/getById/${idModifica}`,
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                $('#walletNameEdit').val(response.name);
                $('#catOptionhtmlEdit').val(response.categoryEntity.id);

                $.ajax({
                    type: "GET",
                    url: "/category/list",
                    contentType: 'application/json',
                    dataType: 'json',
                    headers: {
                        Authorization: sessionStorage.getItem('accessToken')
                    },
                    success: function (resume) {
                        for (let i = resume.length - 1; i >= 0; i--) {
                            $(`<option id='walletSelect' class="roundedCorner" value="${resume[i].id}">${resume[i].name}</option>`).hide().appendTo(optionCategory).fadeIn(i * 20);
                        }
                    }
                })
            }
        });
    });
    //------------------------------------------------------------------------------------
    // END On click on the wallet list of button edit, show wallet name and list category
    //------------------------------------------------------------------------------------

    $('#editWalletConfirm').click(function () {
        const walletEdit = {
            id: idModifica,
            name: $('#walletNameEdit').val(),
            idCategory: $('#catOptionhtmlEdit').val()
        }
        if (editMode) {
            Swal.fire({
                icon: 'question',
                title: '<span style="color:#2D2C2C">Do you want to save ' + walletEdit.name + '?</span>',
                showDenyButton: true,
                showCancelButton: true,
                confirmButtonText: `Save`,
                denyButtonText: `Don't Save`,
            }).then((result) => {
                if (result.isConfirmed) {
                    editWallet(walletEdit);

                } else if (result.isDenied) {
                    Swal.fire("<span style='color:#2D2C2C'>Wallet don't edited!</span>", '', 'info')
                }
            })
        }
    })
    //-------------------------------------------------------------
    // END Modal Get Wallet List Homepage (Edit Wallet)
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Modal Get Wallet List Homepage (Delete Wallet)
    //-------------------------------------------------------------
    function deleteWallet(idDelete) {
        let idPagina = $(`#riga-${idDelete}`);
        $.ajax({
            type: "DELETE",
            url: `/wallet/delete/${idDelete}`,
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (response) {
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
                if (responseDTO === STATEMENT_NOT_FOUND) {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Error, Delete process aborted</span>',
                        text: 'Try again.'
                    })
                }
                if (responseDTO === WALLET_NOT_FOUND) {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Error, Delete process aborted</span>',
                        text: 'Try again.'
                    })
                }
            }
        });
    }

    var idDelete = 0;
    $('#listWallet').on('click', '.btn-elimina-wallet', function () {
        const id = +$(this).attr('data-id');
        idDelete = Number(id);
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
                deleteWallet(idDelete);
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
                swalWithBootstrapButtons.fire(`<span style="color:#2D2C2C">Wallet don't added</span>`, '', 'info')
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
                Swal.fire('<span style="color:#2D2C2C">Saved!</span>', '', 'success')
                setTimeout(function () {
                    window.location.href = 'app-wallet.html';
                }, 2000);
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_TOKEN_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Operation Aborted</span>',
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === TOKEN_REQUIRED) {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Operation Aborted!</span>',
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === INVALID_WALLET_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Operation Aborted!</span>',
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === CATEGORY_NOT_FOUND) {
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

    //-------------------------------------------------------------
    // Invalidate Session on Press Log Out
    //-------------------------------------------------------------
    $('#logout').click(function (e) {
        sessionStorage.removeItem('accessToken');
    });
    //-------------------------------------------------------------
    // END
    //-------------------------------------------------------------
});