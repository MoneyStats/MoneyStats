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
                for (let i = resume.length - 1; i >= 0; i--) {
                    $(`<tr id='riga-${resume[i].id}'>
                <td>${resume[i].name}</td>
                <td>${resume[i].category.name}</td>
                <td>
                    <div class="btn-group roundedCorner" role="group">
                        <button id="btnGroupDrop1" type="button" class="btn btn-outline-primary roundedCorner dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">Opzioni</button>
                            <ul class="dropdown-menu" aria-labelledby="btnGroupDrop1">
                                <li><a class="dropdown-item btn-modifica-wallet" data-bs-toggle="modal" data-bs-target="#editWallet" data-id='${resume[i].id}'>Modifica</a></li>
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
                Swal.fire('Edited!', '', 'success')
                setTimeout(function () {
                    window.location.href = 'homepage.html';
                }, 2000);
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: "Error, Delete process aborted",
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

                $('#editWalletName').val(response.name);
                $('#catOptionhtml').val(response.categoryEntity.id);

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
            name: $('#editWalletName').val(),
            idCategory: $('#catOptionhtmlEdit').val()
        }
        if (editMode) {
            Swal.fire({
                icon: 'question',
                title: 'Do you want to save ' + walletEdit.name + '?',
                showDenyButton: true,
                showCancelButton: true,
                confirmButtonText: `Save`,
                denyButtonText: `Don't Save`,
            }).then((result) => {
                if (result.isConfirmed) {
                    editWallet(walletEdit);

                } else if (result.isDenied) {
                    Swal.fire("Wallet don't edited!", '', 'info')
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
    function deleteWallet(id) {
        let idPagina = $(`#riga-${id}`);
        $.ajax({
            type: "DELETE",
            url: `/wallet/delete/${id}`,
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (response) {
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
                if (responseDTO === STATEMENT_NOT_FOUND) {
                    Swal.fire({
                        icon: 'error',
                        title: "Error, Delete process aborted",
                        text: 'Try again.'
                    })
                }
                if (responseDTO === WALLET_NOT_FOUND) {
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
                setTimeout(function () {
                    window.location.href = 'homepage.html';
                }, 2000);
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_TOKEN_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: "Operation Aborted!",
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === TOKEN_REQUIRED) {
                    Swal.fire({
                        icon: 'error',
                        title: "Operation Aborted!",
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === INVALID_WALLET_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: "Operation Aborted!",
                        text: 'Error during the process.'
                    })
                }
                if (responseDTO === CATEGORY_NOT_FOUND) {
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