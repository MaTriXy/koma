package view

import controller.LoginController
import controller.LoginRequest
import controller.RegisterRequest
import controller.guiEvents
import javafx.collections.FXCollections
import javafx.scene.control.Alert
import javafx.scene.control.ComboBox
import javafx.scene.control.PasswordField
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import koma.gui.view.window.preferences.loginconf.LoginConfWindow
import koma.matrix.user.identity.UserId_new
import koma.storage.Recent
import koma.storage.config.server.get_server_proxy
import matrix.UserRegistering
import rx.javafx.kt.actionEvents
import rx.javafx.kt.addTo
import rx.javafx.kt.toObservableNonNull
import rx.lang.kotlin.filterNotNull
import tornadofx.*
import util.getRecentUsers

/**
 * Created by developer on 2017/6/21.
 */
class LoginScreen(): View() {

    override val root = VBox()
    val controller = LoginController()

    var userId: ComboBox<String> by singleAssign()
    var serverCombo: ComboBox<String> by singleAssign()
    var password: PasswordField by singleAssign()

    init {
        title = "Koma"

        val grid = GridPane()
        with(grid) {
            paddingAll = 5.0
            row("Username") {
                val recentUsers = getRecentUsers().map { it.toString() }
                userId = combobox(values = recentUsers) {
                    isEditable = true
                    selectionModel.selectFirst()

                }
            }
            row("Server") {
                val serverCommonUrls = listOf("https://matrix.org")
                serverCombo = combobox(values = serverCommonUrls) {
                    isEditable = true
                    selectionModel.select(0)
                }
            }
            row("Password") {
                password = passwordfield() {
                }
            }
        }
        with(root) {
            add(grid)

            val serverName = stringBinding(userId.valueProperty()) { if (value != null && value.isNotBlank()) UserId_new(value)?.server else null }
            val settings = LoginConfWindow(serverName)
            button("More Options") {
                action { settings.openModal() }
            }
            buttonbar {
                button("Register") {
                    actionEvents()
                            .map {
                                val userid = UserId_new(userId.value)
                                if ( userid == null) {
                                    alert(Alert.AlertType.WARNING, "Invalid user-id")
                                    null
                                } else if (password.text.isBlank()) {
                                    alert(Alert.AlertType.WARNING, "Invalid password")
                                    null
                                } else {
                                    RegisterRequest(
                                            serverCombo.editor.text,
                                            get_server_proxy(userid.server),
                                            UserRegistering(
                                                    userid.user,
                                                    password.text
                                            )
                                    )
                                }
                            }
                            .filterNotNull()
                            .addTo(guiEvents.registerRequests)
                }
                button("Login") {
                    isDefaultButton = true
                    actionEvents().map { UserId_new(userId.value) }.filterNotNull() .map {
                        LoginRequest(
                                it,
                                serverCombo.editor.text,
                                if (password.text.isNotEmpty()) password.text else null,
                                get_server_proxy(it.server))
                    }.addTo(guiEvents.loginRequests)
                }
            }
        }

        set_up_listeners()
    }

    private fun set_up_listeners(){
        userId.selectionModel.selectedItemProperty().toObservableNonNull()
                            .map{ UserId_new(it) }
                            .filterNotNull()
                            .map { Recent.server_addrs.get(it.server)}
                            .filterNotNull()
                            .subscribe {
                                serverCombo.items = FXCollections.observableArrayList(it)
                                serverCombo.selectionModel.selectFirst()
                            }
    }
}
