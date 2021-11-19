$(document).ready(function () {

    const LOGIN_REQUIRED = "LOGIN_REQUIRED";
    const ADMIN_ROLE = "ADMIN";

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
                // GET Role for render pages
                localStorage.setItem('user-role', authCredentialDTO.role);
                if (authCredentialDTO.role === ADMIN_ROLE) {
                    renderDatabase();
                }
            },
            error: function () {
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
        })
    }

    //-------------------------------------------------------------
    // Render Database
    //-------------------------------------------------------------
    function renderDatabase() {
        const render = $('#renderDatabase');
        $(`<div class="listview-title mt-1">Admin Function</div>
        <ul class="listview image-listview text mb-2 inset">
        <li>
        <a href="#" class="item" data-bs-toggle="modal" data-bs-target="#backupDatabaseModal">
            <div class="in">
                <div>Backup Database</div>
            </div>
        </a>
    </li>
    <li>
        <a href="#" class="item" data-bs-toggle="modal" data-bs-target="#restoreDatabaseModal">
            <div class="in">
                <div>Restore Database</div>
            </div>
        </a>
    </li></ul>`).appendTo(render);
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
                    showConfirmButton: false
                })
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

    //-------------------------------------------------------------
    // Update Password User
    //-------------------------------------------------------------
    const PASSWORD_NOT_MATCH = "PASSWORD_NOT_MATCH";
    const WRONG_CREDENTIAL = "WRONG_CREDENTIAL";

    $('#updatePassBtn').click(function () {
        editMode = true;
        const authChangePasswordInputDTO = {
            username: localStorage.getItem('username'),
            oldPassword: $('#oldPassword').val(),
            newPassword: $('#newPassword').val(),
            confirmNewPassword: $('#newPasswordCheck').val()
        }
        if (editMode) {
            Swal.fire({
                icon: 'question',
                title: '<span style="color:#2D2C2C">Do you want to save the current Password?</span>',
                showDenyButton: true,
                showCancelButton: true,
                confirmButtonText: `Save`,
                denyButtonText: `Don't Save`,
            }).then((result) => {
                if (result.isConfirmed) {
                    changePassword(authChangePasswordInputDTO);
                } else if (result.isDenied) {
                    Swal.fire('<span style="color:#2D2C2C">Operation Aborted!</span>', '', 'info')
                }
            })
        }
    })

    function changePassword(authChangePasswordInputDTO) {
        $.ajax({
            type: "PUT",
            url: `/credential/update/password`,
            data: JSON.stringify(authChangePasswordInputDTO),
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
                    showConfirmButton: false
                })
                setTimeout(function () {
                    window.location.href = 'app-settings.html';
                }, 1000);
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === PASSWORD_NOT_MATCH) {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Error!</span>',
                        text: "The password insert don't match"
                    })
                }
                if (responseDTO === WRONG_CREDENTIAL) {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Error!</span>',
                        text: "The password insert is not equal to the old password"
                    })
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '<span style="color:#2D2C2C">Error!</span>',
                        text: 'Process aborted, try again.'
                    })
                }
            }
        });
    }

    //-------------------------------------------------------------
    // END Update Password User
    //-------------------------------------------------------------

    /*--------------------------------------------------------------------------
    *  Switch Desktop Mode
    *--------------------------------------------------------------------------*/
    $('#SwitchDesktop').click(function () {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1000,
            timerProgressBar: true,
        })
        Toast.fire({
            icon: 'info',
            title: "<span style='color:#2D2C2C'>Reidirect...</span>",
        })
        setTimeout(function () {
            window.location.href = "../homepage.html";
        }, 1000);
    });
    /*--------------------------------------------------------------------------
     *  Switch Desktop Mode
     *--------------------------------------------------------------------------*/

    //------------------------------------------------------------------------------------
    // On click of Backup Database
    //------------------------------------------------------------------------------------
    const BACKUP_DATABASE = "EXPORT_DUMP_COMMAND";
    var USER_ROLE = localStorage.getItem('user-role');
    $('#backup-database-btn').click(function () {
        const databaseCommandDTO = {
            //filePath: $('#filepath').val(),
            database: BACKUP_DATABASE,
            role: USER_ROLE
        }
        Swal.fire({
            icon: 'question',
            title: "<span style='color:#2D2C2C'>Do you want to Backup the Database?</span>",
            showDenyButton: true,
            showCancelButton: true,
            confirmButtonText: `Save`,
            denyButtonText: `Don't Save`,
        }).then((result) => {
            if (result.isConfirmed) {
                backupDatabase(databaseCommandDTO);
            } else if (result.isDenied) {
                Swal.fire("Operation Aborted!", '', 'info')
            }
        })
    })

    function backupDatabase(databaseCommandDTO) {
        $.ajax({
            type: "POST",
            url: `/database/exportDatabase`,
            data: JSON.stringify(databaseCommandDTO),
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function () {
                Swal.fire({
                    title: "<span style='color:#2D2C2C'>Backup Completed!</span>",
                    icon: 'success',
                    showConfirmButton: false
                })
                setTimeout(function () {
                    window.location.href = 'app-settings.html';
                }, 1000);
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: "<span style='color:#2D2C2C'>Error</span>",
                    text: 'Backup aborted, try again.'
                })
            }
        });
    }

    //-------------------------------------------------------------
    // END On click of Backup Database
    //-------------------------------------------------------------ù

    //------------------------------------------------------------------------------------
    // On click of Restore Database
    //------------------------------------------------------------------------------------
    const RESTORE_DATABASE = "IMPORT_DUMP_COMMAND";
    
    getFolderData();
    function getFolderData() {
        $.ajax({
            type: "GET",
            url: `/database/getBackupFolder`,
            contentType: 'application/json',
            dataType: 'json',
            success: function (folder) {
                const render = $('#listFolder');
                for (let i = 0; i < folder.length; i++) {
                    $(`<div class="form-check">
                    <input type="checkbox" class="form-check-input folderSelect" id="customCheckd${i}" value="${folder[i]}">
                    <label class="form-check-label" for="customCheckd${i}">Backup_${folder[i]}</label>
                </div>`).appendTo(render);
                }
            }
        });
    }

    $('#restore-btn').click(function () {
        console.log($('.folderSelect').val())
        const databaseCommandDTO = {
            filePath: $('.folderSelect').val(),
            database: RESTORE_DATABASE,
            role: USER_ROLE
        }
        Swal.fire({
            icon: 'question',
            title: "<span style='color:#2D2C2C'>Do you want to Restore the Current Database?</span>",
            showDenyButton: true,
            showCancelButton: true,
            confirmButtonText: `Save`,
            denyButtonText: `Don't Save`,
        }).then((result) => {
            if (result.isConfirmed) {
                importDatabase(databaseCommandDTO);
            } else if (result.isDenied) {
                Swal.fire("<span style='color:#2D2C2C'>Operation Aborted!</span>", '', 'info')
            }
        })
    })

    function importDatabase(databaseCommandDTO) {
        $.ajax({
            type: "POST",
            url: `/database/importDatabase`,
            data: JSON.stringify(databaseCommandDTO),
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                Authorization: sessionStorage.getItem('accessToken')
            },
            success: function () {
                Swal.fire({
                    title: "<span style='color:#2D2C2C'>Restore Completed!</span>",
                    icon: 'success',
                    showConfirmButton: false
                })
                setTimeout(function () {
                    window.location.href = 'settings.html';
                }, 1000);
            },
            error: function () {
                Swal.fire({
                    icon: 'error',
                    title: "<span style='color:#2D2C2C'>Error</span>",
                    text: 'Restore aborted, try again.'
                })
            }
        });
    }

    //-------------------------------------------------------------
    // END On click of Restore Database
    //-------------------------------------------------------------
});