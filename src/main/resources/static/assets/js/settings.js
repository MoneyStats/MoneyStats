$(document).ready(function () {

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
                localStorage.setItem('user-role', authCredentialDTO.role);
                if (authCredentialDTO.role === ADMIN_ROLE) {
                    renderDatabase();
                }
            },
            error: function (errorResponse) {
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
                    showConfirmButton: false
                })
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
                title: 'Do you want to save the current Password?',
                showDenyButton: true,
                showCancelButton: true,
                confirmButtonText: `Save`,
                denyButtonText: `Don't Save`,
            }).then((result) => {
                if (result.isConfirmed) {
                    changePassword(authChangePasswordInputDTO);
                } else if (result.isDenied) {
                    Swal.fire("Operation Aborted!", '', 'info')
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
                    title: 'Edited!',
                    icon: 'success',
                    showConfirmButton: false
                })
                setTimeout(function () {
                    window.location.href = 'homepage.html';
                }, 1000);
            },
            error: function (authErrorResponseDTO) {
                var responseDTO = authErrorResponseDTO.responseJSON.message;
                if (responseDTO === PASSWORD_NOT_MATCH) {
                    Swal.fire({
                        icon: 'error',
                        title: "Error!",
                        text: "The password insert don't match"
                    })
                }
                if (responseDTO === WRONG_CREDENTIAL) {
                    Swal.fire({
                        icon: 'error',
                        title: "Error!",
                        text: "The password insert is not equal to the old password"
                    })
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: "Error",
                        text: 'Process aborted, try again.'
                    })
                }
            }
        });
    }

    //-------------------------------------------------------------
    // END Update Password User
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    // Render Database
    //-------------------------------------------------------------
    function renderDatabase() {
        const render = $('#renderDatabase');
        $(`<a class="nav-link" id="database-tab" data-toggle="pill" href="#database" role="tab"
                       aria-controls="database" aria-selected="false">
                        <i class="fa fa-database text-center mr-1"></i>
                        Database
                    </a>`).appendTo(render);
    }

    //-------------------------------------------------------------
    // Reset Tabs
    //-------------------------------------------------------------
    $('#account-tab').click(function () {
        $('#database-tab').attr("class", "nav-link");
        $('#database-tab').attr("aria-selected", "false");
    })
    $('#password-tab').click(function () {
        $('#database-tab').attr("class", "nav-link");
        $('#database-tab').attr("aria-selected", "false");
    })
    //------------------------------------------------------------------------------------
    // On click of Backup Database
    //------------------------------------------------------------------------------------
    const BACKUP_DATABASE = "EXPORT_DUMP_COMMAND";
    var USER_ROLE = localStorage.getItem('user-role');
    $('#backup-database-btn').click(function () {
        const databaseCommandDTO = {
            filePath: "backup/database/",
            database: BACKUP_DATABASE,
            role: USER_ROLE
        }
        Swal.fire({
            icon: 'question',
            title: 'Do you want to Backup the Database?',
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
                    title: 'Backup Completed!',
                    icon: 'success',
                    showConfirmButton: false
                })
                setTimeout(function () {
                    window.location.href = 'settings.html';
                }, 1000);
            },
            error: function (error) {
                Swal.fire({
                    icon: 'error',
                    title: "Error",
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
    $('#restore-database-btn').click(function () {
        $("#listFolder").html("");
        getFolderData();
    })

    function getFolderData() {
        $.ajax({
            type: "GET",
            url: `/database/getBackupFolder`,
            contentType: 'application/json',
            dataType: 'json',
            success: function (folder) {
                const render = $('#listFolder');
                for (let i = 0; i < folder.length; i++) {
                    $(`<tr>
                    <td class='space folderSelect' style='margin-left: 0px;'><div class="form-check">
                    <input id="folderSelect" class="form-check-input" type="radio" name="flexRadioDefault" id="flexRadioDefault1" value="${folder[i]}">
                    <label class="form-check-label" for="flexRadioDefault1">
                    Backup_${folder[i]}
                    </label>
                  </div></td></tr>`).appendTo(render);
                }
            }
        });
    }

    $('#restore-btn').click(function () {
        const databaseCommandDTO = {
            filePath: $('#folderSelect').val(),
            database: RESTORE_DATABASE,
            role: USER_ROLE
        }
        Swal.fire({
            icon: 'question',
            title: 'Do you want to Restore the Current Database?',
            showDenyButton: true,
            showCancelButton: true,
            confirmButtonText: `Save`,
            denyButtonText: `Don't Save`,
        }).then((result) => {
            if (result.isConfirmed) {
                importDatabase(databaseCommandDTO);
            } else if (result.isDenied) {
                Swal.fire("Operation Aborted!", '', 'info')
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
                    title: 'Restore Completed!',
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
                    title: "Error",
                    text: 'Restore aborted, try again.'
                })
            }
        });
    }

    //-------------------------------------------------------------
    // END On click of Restore Database
    //-------------------------------------------------------------
});