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
                    title: 'INSERITO!',
                    text: 'Utente inserito Correttamente',
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
                        title: "Errore, Non è possibile aggiungere l'utente",
                        text: 'Controlla i dati e riprova.'
                    })
                }
                if (responseDTO === DATABASE_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Errore Interno",
                        text: 'Riprova Più tardi.'
                    })
                }
                if (responseDTO === USER_PRESENT) {
                    Swal.fire({
                        icon: 'error',
                        title: "Errore, Non è possibile aggiungere l'utente",
                        text: "Lo username inserito è già presente, riprova con un'altro username."
                    })
                }
                if (responseDTO === INTERNAL_SERVER_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Errore Interno",
                        text: 'Riprova Più tardi.'
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
        Swal.fire({
            title: 'Sicuro di Salvare?',
            text: "Confermare registrazione?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Registrati'
        }).then((result) => {
            if (result.isConfirmed) {
                addUser(authCredentialDTO);
            }
        })

        $('#firstName_desktop').val('');
        $('#lastName_desktop').val('');
        $('#dateOfBirth_desktop').val('');
        $('#email_desktop').val('');
        $('#username_desktop').val('');
        $('#password_desktop').val('');
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
                    title: 'Credenziali corrette!',
                    text: `Benvenuto ${userLogged}`,
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
                        title: "Errore!",
                        text: 'Controlla i dati e riprova.'
                    })
                }
                if (responseDTO === DATABASE_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Errore Interno",
                        text: 'Riprova Più tardi.'
                    })
                }
                if (responseDTO === WRONG_CREDENTIAL) {
                    Swal.fire({
                        icon: 'error',
                        title: "Credenziali Errare",
                        text: "Username o Password errati, riprova."
                    })
                }
                if (responseDTO === INTERNAL_SERVER_ERROR) {
                    Swal.fire({
                        icon: 'error',
                        title: "Errore Interno",
                        text: 'Riprova Più tardi.'
                    })
                }
            }
        });
    }
});