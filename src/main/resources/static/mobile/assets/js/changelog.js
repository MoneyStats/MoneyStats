$(document).ready(function () {
    const tag = "v210";
    const VERSION = "2.1.0";
    const CURRENT_VERSION = "Moneystats " + VERSION;
    const DATE_CURRENT_VERSION = "23-11-2021 - " + CURRENT_VERSION;
    var DESCRIPTION = `<strong class="fs-3">${VERSION}</strong><br>
    - Fixed Bug Sign Up. <br>
    - Fixed Language Bug. <br>
    - Fixed Mobile Info view on IPad. <br>
    - Fixed StatementByDate don't show Info Tab. <br>
    - Fixed Backup and Restore Implementations. <br>
    - Added new changelog <br>`;

    // Old Version
    const tag_2 = "v2";
    const VERSION_2 = "2.0.0";
    const CURRENT_VERSION_2 = "Moneystats " + VERSION_2;
    const DATE_CURRENT_VERSION_2 = "19-11-2021 - " + CURRENT_VERSION_2;
    var DESCRIPTION_2 = `<strong class="fs-3">${VERSION_2}</strong><br>
    - Added 2 password verification on Sign up. <br>
    - Fixed Sign up if don't put all values. <br>
    - Added email check, if email is already present it would give an error. <br>
    - Fixed homepage style on first boot. <br>
    - Update Changelog to V2.0.0. <br>
    - Added option to backup and restore the Database(Only Admin user) <br>
    - Fixed Bugs <br>
    - Added Settings Page. <br>
    - Added options to change user profile. <br>
    - Backend Log Improvement. <br>
    - Publish on DockerHub Working. <br>
    - Changelog Page Refactor.`;

    const tag_1 = "v1";
    const VERSION_1 = "1.0.0";
    const DATE_CURRENT_VERSION_1 = "19-09-2021 - MoneyStats V1.0.0";
    var DESCRIPTION_1 = `<strong class="fs-3">${VERSION_1}</strong><br>
    - First Release.`;
    renderChangelog();

    //-------------------------------------------------------------
    // Render Changelog
    //-------------------------------------------------------------
    function renderChangelog() {
        const render = $('#renderChangelog');
        $(`<!-- MoneyStats Info Action Sheet -->
         <div class="modal fade modalbox" id="infoActionSheet" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">MoneyStats Info</h5>
                        <a href="javascript:;" data-bs-dismiss="modal">Close</a>
                    </div>
                    <div class="modal-body">
                        <div class="section mt-2 mb-2">
                            <div class="captionCenter">
                                <div class="card" style="max-width: 400px;">
                                    <img src="./assets/img/logos/logo_transparent.png" style="height: 100px;" class="responsive"
                                         alt="logo_transparent">
                                    <hr>
                                    <p class="text-center">${CURRENT_VERSION}</p>
                                    <button type="button" data-bs-toggle="modal" data-bs-target="#changelogActionSheet"
                                            class="btn btn-text-primary rounded shadowed me-1 mb-1">Changelog
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- * MoneyStats Info Action Sheet -->
        <!-- MoneyStats Changelog Action Sheet -->
                <div class="modal fade modalbox" id="changelogActionSheet" tabindex="-1" role="dialog">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">${CURRENT_VERSION}</h5>
                                <a href="javascript:;" data-bs-dismiss="modal">Close</a>
                            </div>
                            <div class="modal-body">
                                <div class="section mt-2 mb-2">
                                    <div class="section-title">Changelog</div>
                                    <div class="accordion" id="accordionChangelog">
                                        <!-- Accordion -->
                                        <div class="accordion-item">
                                            <h2 class="accordion-header">
                                                <button class="accordion-button" type="button" data-bs-toggle="collapse"
                                                        data-bs-target="#${tag}">
                                                    ${DATE_CURRENT_VERSION}
                                                </button>
                                            </h2>
                                            <div id="${tag}" class="accordion-collapse collapse show" data-bs-parent="#accordionChangelog">
                                                <div class="accordion-body">
                                                    ${DESCRIPTION}
                                                </div>
                                            </div>
                                        </div>
                                        <!-- Accordion -->
                                        <!-- Accordion -->
                                        <div class="accordion-item">
                                            <h2 class="accordion-header">
                                                <button class="accordion-button collapsed" type="button" data-bs-toggle="${tag_2}"
                                                        data-bs-target="#${tag_2}">
                                                    ${DATE_CURRENT_VERSION_2}
                                                </button>
                                            </h2>
                                            <div id="${tag_2}" class="accordion-collapse collapse" data-bs-parent="#accordionChangelog">
                                                <div class="accordion-body">
                                                    ${DESCRIPTION_2}
                                                </div>
                                            </div>
                                        </div>
                                        <!-- Accordion -->
                                        <!-- Accordion -->
                                        <div class="accordion-item">
                                            <h2 class="accordion-header">
                                                <button class="accordion-button collapsed" type="button" data-bs-toggle="${tag_1}"
                                                        data-bs-target="#${tag_1}">
                                                        ${DATE_CURRENT_VERSION_1}
                                                </button>
                                            </h2>
                                            <div id="${tag_1}" class="accordion-collapse collapse" data-bs-parent="#accordionChangelog">
                                                <div class="accordion-body">
                                                    ${DESCRIPTION_1}
                                                </div>
                                            </div>
                                        </div>
                                        <!-- Accordion -->
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- * MoneyStats Info Action Sheet -->`).appendTo(render);
    }
});