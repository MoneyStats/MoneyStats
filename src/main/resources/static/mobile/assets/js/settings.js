$(document).ready(function () {

    const LOGIN_REQUIRED = "LOGIN_REQUIRED";

    //-------------------------------------------------------------
    // Check if session is validated with a user
    //-------------------------------------------------------------
    isValidated();

    function isValidated() {
        const token = sessionStorage.getItem('accessToken');
        if (token === null) {
            window.location.href = 'loginpage.html';
        }
        //-------------------------------------------------------------
        // Check if session is validated with a user
        //-------------------------------------------------------------
        $.ajax({
            type: "GET",
            url: "/credential/getCurrentUser",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (authCredentialDTO) {
                $('#nameSurname').text(`Update ${authCredentialDTO.firstName} ${authCredentialDTO.lastName}`);
                $('#nameSettings').val(`${authCredentialDTO.firstName}`);
                $('#surnameSettings').val(`${authCredentialDTO.lastName}`);
                $('#emailsSettings').val(`${authCredentialDTO.email}`);
                $('#dateOfBirthSettings').val(`${authCredentialDTO.dateOfBirth}`);
                localStorage.setItem('username', authCredentialDTO.username);
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
                        title: '<span style="color:#2D2C2C">Expired Session, reidirect...</span>'
                    })
                    setTimeout(function () {
                        window.location.href = "app-login.html";
                    }, 1500);
                }
            }
        })
    }

    //------------------------------------------------------------------------------------
    // On click of Update user it will take all the information need it
    //------------------------------------------------------------------------------------
    $('#updateAuthBtn').click(function () {
        updateUser();
    });
    $('#updateEmail').click(function () {
        updateUser();
    });
    function updateUser() {
        editMode = true;
        const authCredentialToUpdateDTO = {
            firstName: $('#nameSettings').val(),
            lastName: $('#surnameSettings').val(),
            dateOfBirth: $('#dateOfBirthSettings').val(),
            email: $('#emailsSettings').val(),
            username: localStorage.getItem('username')
        }
        if (editMode) {
            Swal.fire({
                icon: 'question',
                title: '<span style="color:#2D2C2C">Do you want to save?</span>',
                showDenyButton: true,
                showCancelButton: true,
                confirmButtonText: `Save`,
                denyButtonText: `Don't Save`,
            }).then((result) => {
                if (result.isConfirmed) {
                    editUser(authCredentialToUpdateDTO);
                } else if (result.isDenied) {
                    Swal.fire('<span style="color:#2D2C2C">Operation Aborted!</span>', '', 'info')
                }
            })
        }
    }
    //-------------------------------------------------------------
    // END On click of Update user it will take all the information need it
    //-------------------------------------------------------------

    let editMode = false;

    function editUser(authCredentialToUpdateDTO) {
        $.ajax({
            type: "PUT",
            url: `/credential/update`,
            data: JSON.stringify(authCredentialToUpdateDTO),
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function () {
                editMode = false;
                Swal.fire({
                    title: '<span style="color:#2D2C2C">Edited!</span>',
                    icon: 'success',
                showConfirmButton: false})
                setTimeout(function () {
                    window.location.href = 'index.html';
                }, 1000);
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: '<span style="color:#2D2C2C">Error</span>',
                    text: 'Process aborted, try again.'
                })
            }
        });
        localStorage.removeItem('username');
    }
});