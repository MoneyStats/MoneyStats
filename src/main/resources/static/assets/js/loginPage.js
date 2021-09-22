$(document).ready(function () {

    /*--------------------------------------------------------------------------
    *  Variables to handle the possible exception during signUp process
    *--------------------------------------------------------------------------*/
    const INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    const USER_PRESENT = "USER_PRESENT";
    const WRONG_CREDENTIAL = "WRONG_CREDENTIAL";
    const INVALID_AUTH_INPUT_DTO = "INVALID_AUTH_INPUT_DTO";
    const INVALID_AUTH_CREDENTIAL_DTO = "INVALID_AUTH_CREDENTIAL_DTO";
    const DATABASE_ERROR = "DATABASE_ERROR";

    /*--------------------------------------------------------------------------
    *  SignUp Process Start
    *--------------------------------------------------------------------------*/
    function addUser(authCredentialDTO) {
        $.ajax({
            type: "POST",
            url: "/credential/signup",
            data: JSON.stringify(authCredentialDTO),
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Insert!',
                    text: 'User Added Correctly',
                    showConfirmButton: false,
                    timer: 1000
                }),
                    setTimeout(function (render) {
                        window.location.href = 'loginpage.html';
                    }, 1000)
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_AUTH_CREDENTIAL_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: "Error, is not possible to add the User",
                        text: 'Check Data and try again.'
                    })
                }
                if (responseDTO === DATABASE_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Internal Error",
                        text: 'Try later.'
                    })
                }
                if (responseDTO === USER_PRESENT) {
                    Swal.fire({
                        icon: 'error',
                        title: "Error",
                        text: "The username is already present, try with another username."
                    })
                    return;
                }
                if (responseDTO === INTERNAL_SERVER_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Internal Error",
                        text: 'Try later.'
                    })
                }
            }
        });
    }

    // DESKTOP
    $('#signUpBtn_desktop').click(function () {
        const authCredentialDTO = {
            firstName: $('#firstName_desktop').val(),
            lastName: $('#lastName_desktop').val(),
            dateOfBirth: $('#dateOfBirth_desktop').val(),
            email: $('#email_desktop').val(),
            username: $('#username_desktop').val(),
            password: $('#password_desktop').val()
        }
        var checkPassword = $('#check_password_desktop').val();
        var emptyValue = "";
        if (authCredentialDTO.firstName === emptyValue || authCredentialDTO.lastName === emptyValue || authCredentialDTO.dateOfBirth === emptyValue || authCredentialDTO.email === emptyValue) {
            return;
        }
        if (authCredentialDTO.password != checkPassword) {
            const Toast = Swal.mixin({
                toast: true,
                position: 'center',
                showConfirmButton: false,
                timer: 1000,
                timerProgressBar: true,
                didOpen: (toast) => {
                    toast.addEventListener('mouseenter', Swal.stopTimer)
                    toast.addEventListener('mouseleave', Swal.resumeTimer)
                }
            })

            Toast.fire({
                icon: 'error',
                title: "Password don't match, try again"
            })
            return;
        }
        Swal.fire({
            title: 'Do you want to save?',
            text: "Confirm to register Current User",
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sign up'
        }).then((result) => {
            if (result.isConfirmed) {
                addUser(authCredentialDTO);
                $('#firstName_desktop').val('');
                $('#lastName_desktop').val('');
                $('#dateOfBirth_desktop').val('');
                $('#email_desktop').val('');
                $('#username_desktop').val('');
                $('#password_desktop').val('');
                $('#check_password_desktop').val('');
            } else {
                return;
            }
        })
    })
    // SignUp process end

    /*--------------------------------------------------------------------------
    *  Login Process Start
    *--------------------------------------------------------------------------*/
    var userLogged = '';
    // DESKTOP
    $('#loginBtn_desktop').click(function () {
        const authCredentialInputDTO = {
            username: $('#username_login_desktop').val(),
            password: $('#password_login_desktop').val()
        }
        userLogged = authCredentialInputDTO.username;
        login(authCredentialInputDTO);

        $('#username_login_desktop').val('');
        $('#password_login_desktop').val('');
    })

    // Login Process Start
    function login(authCredentialInputDTO) {
        $.ajax({
            type: "POST",
            url: "/credential/login",
            data: JSON.stringify(authCredentialInputDTO),
            contentType: 'application/json',
            dataType: 'json',
            success: function (tokenDTO) {
                var accessToken = tokenDTO.accessToken;
                Swal.fire({
                    icon: 'success',
                    title: 'Correct Credential!',
                    text: `Welcome ${userLogged}`,
                    showConfirmButton: false,
                    timer: 1500
                }),
                    sessionStorage.setItem('accessToken', accessToken);

                setTimeout(function (render) {
                    window.location.href = 'homepage.html';
                }, 3000)
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_AUTH_INPUT_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: "Error!",
                        text: 'Check data and try again.'
                    })
                }
                if (responseDTO === DATABASE_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Internal Error",
                        text: 'Try again.'
                    })
                }
                if (responseDTO === WRONG_CREDENTIAL) {
                    Swal.fire({
                        icon: 'error',
                        title: "Wrong Credential",
                        text: "Wrong username or password, try again."
                    })
                    return;
                }
                if (responseDTO === INTERNAL_SERVER_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Internal Error",
                        text: 'Try again.'
                    })
                }
            }
        });
    }
});