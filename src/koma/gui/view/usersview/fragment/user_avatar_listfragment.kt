package koma.gui.view.usersview.fragment

import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.HBox
import javafx.util.Callback
import koma.model.user.UserState
import tornadofx.*

fun get_cell_property(noname: SimpleBooleanProperty): ObjectBinding<Callback<ListView<UserState>, ListCell<UserState>>?> {
    val small_cell = object: Callback<ListView<UserState>, ListCell<UserState>> {
        override fun call(param: ListView<UserState>?): ListCell<UserState> {
            return UserAvatarCell()
        }
    }

    val full_cell = object: Callback<ListView<UserState>, ListCell<UserState>> {
        override fun call(param: ListView<UserState>?): ListCell<UserState> {
            return UserFullCell()
        }
    }

    val observable = objectBinding(noname) { if (value) { small_cell } else full_cell}
    return observable
}

class UserAvatarCell : ListCell<UserState>() {

    override fun updateItem(item: UserState?, empty: Boolean) {
        super.updateItem(item, empty)

        graphic = get_node(item, false)
    }
}

class UserFullCell : ListCell<UserState>() {

    override fun updateItem(item: UserState?, empty: Boolean) {
        super.updateItem(item, empty)

        graphic = get_node(item, true)
    }
}

private fun get_node(item: UserState?, showName: Boolean): Node {
    val root = HBox( 5.0)
    if (item == null)
        return root
    root.apply {
        minWidth = 1.0
        prefWidth = 1.0
        style { alignment = Pos.CENTER_LEFT }
        stackpane {
            imageview {
                this.imageProperty().bind(item.avatarImgProperty)
                isCache = true
                isPreserveRatio = true
            }
            minHeight = 32.0
        }
        if (showName) {
            label(item.displayName) {
                this.textFillProperty().bind(item.colorProperty)
            }
        }
    }
    return root
}

