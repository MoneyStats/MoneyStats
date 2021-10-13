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
            url: "credential/getCurrentUser",
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function (authCredentialDTO) {
                $('#nameSurname').text(`${authCredentialDTO.firstName} ${authCredentialDTO.lastName}`);
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
                        title: 'Expired Session, reidirect...'
                    })
                    setTimeout(function () {
                        window.location.href = "loginpage.html";
                    }, 1500);
                }
            }
        })
    }

    //------------------------------------------------------------------------------------
    // On click of Update user it will take all the information need it
    //------------------------------------------------------------------------------------
    $('#updateAuthBtn').click(function () {
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
                title: 'Do you want to save?',
                showDenyButton: true,
                showCancelButton: true,
                confirmButtonText: `Save`,
                denyButtonText: `Don't Save`,
            }).then((result) => {
                if (result.isConfirmed) {
                    editUser(authCredentialToUpdateDTO);
                } else if (result.isDenied) {
                    Swal.fire("Operation Aborted!", '', 'info')
                }
            })
        }
    })
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
                    title: 'Edited!',
                    icon: 'success',
                showConfirmButton: false})
                setTimeout(function () {
                    window.location.href = 'homepage.html';
                }, 1000);
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: "Error",
                    text: 'Process aborted, try again.'
                })
            }
        });
        localStorage.removeItem('username');
    }
});