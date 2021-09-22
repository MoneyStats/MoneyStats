$(document).ready(function () {

    /* Variables to handle the possible exception during signUp process */
    const INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    const USER_PRESENT = "USER_PRESENT";
    const WRONG_CREDENTIAL = "WRONG_CREDENTIAL";
    const INVALID_AUTH_INPUT_DTO = "INVALID_AUTH_INPUT_DTO";
    const INVALID_AUTH_CREDENTIAL_DTO = "INVALID_AUTH_CREDENTIAL_DTO";
    const DATABASE_ERROR = "DATABASE_ERROR";

    // SignUp Process Start
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
                    title: '<span style="color:#2D2C2C">Insert!</span>',
                    text: 'User insert correctly',
                    showConfirmButton: false,
                    timer: 1000
                }),
                    setTimeout(function (render) {
                        window.location.href = 'app-login.html';
                    }, 1000)
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_AUTH_CREDENTIAL_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Error, is not possible to add the User</span>",
                        text: 'Check Data and try again.'
                    })
                }
                if (responseDTO === DATABASE_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Internal Error</span>",
                        text: 'Try later.'
                    })
                }
                if (responseDTO === USER_PRESENT) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Error, is not possible to add the Use</span>r",
                        text: "The username isert is already present, try with another username."
                    })
                }
                if (responseDTO === INTERNAL_SERVER_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Internal Error</span>",
                        text: 'Try later.'
                    })
                }
            }
        });
    }

    //MOBILE
    $('#signUpBtn_mobile').click(function () {
        const authCredentialDTO = {
            firstName: $('#firstName_mobile').val(),
            lastName: $('#lastName_mobile').val(),
            dateOfBirth: $('#dateOfBirth_mobile').val(),
            email: $('#email_mobile').val(),
            username: $('#username_mobile').val(),
            password: $('#password_mobile').val()
        }
        var checkPassword = $('#password2').val();
        var emptyValue = "";
        if (authCredentialDTO.firstName === emptyValue || authCredentialDTO.lastName === emptyValue || authCredentialDTO.dateOfBirth === emptyValue || authCredentialDTO.email === emptyValue){
            return;
        }
        if (authCredentialDTO.password != checkPassword){
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
                title: "<span style='color:#2D2C2C'>Password don't match, try again</span>"
              })
              return;
        }
        Swal.fire({
            title: "<span style='color:#2D2C2C'>Do you want to save?</span>",
            text: "Confirm to register Current User",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sign up'
        }).then((result) => {
            if (result.isConfirmed) {
                addUser(authCredentialDTO);
                $('#firstName_mobile').val('');
                $('#lastName_mobile').val('');
                $('#dateOfBirth_mobile').val('');
                $('#email_mobile').val('');
                $('#username_mobile').val('');
                $('#password_mobile').val('');
                $('#password2').val('');
            }
        })

        
    })
// SignUp process end

// Login Process Start
    //MOBILE

    var userLogged = '';
    // MOBILE
    $('#loginBtn_mobile').click(function () {
        const authCredentialInputDTO = {
            username: $('#username_login_mobile').val(),
            password: $('#password_login_mobile').val()
        }
        userLogged = authCredentialInputDTO.username;
        login(authCredentialInputDTO);

        $('#username_login_mobile').val('');
        $('#password_login_mobile').val('');
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
                    title: '<span style="color:#2D2C2C">Correct Credential!</span>',
                    text: `Welcome ${userLogged}`,
                    showConfirmButton: false,
                    timer: 1500
                }),
                    sessionStorage.setItem('accessToken', accessToken);

                setTimeout(function (render) {
                    window.location.href = 'index.html';
                }, 3000)
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === INVALID_AUTH_INPUT_DTO) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Error!</span>",
                        text: 'Check your data and try again.'
                    })
                }
                if (responseDTO === DATABASE_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Internal Error</span>",
                        text: 'Try again later.'
                    })
                }
                if (responseDTO === WRONG_CREDENTIAL) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Wrong Credential</span>",
                        text: "Username o Password missmatch, try again."
                    })
                }
                if (responseDTO === INTERNAL_SERVER_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "<span style='color:#2D2C2C'>Internal Error</span>",
                        text: 'Try again later.'
                    })
                }
            }
        });
    }
});