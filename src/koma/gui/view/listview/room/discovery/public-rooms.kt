package koma.gui.view.listview.room.discovery

import domain.DiscoveredRoom
import gui.view.listview.room.discovery.DiscoveredRoomFragment
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.VBox
import rx.javafx.kt.toObservable
import rx.lang.kotlin.filterNotNull
import tornadofx.*

class PublicRoomsView(val publicRoomList: ObservableList<DiscoveredRoom>, val joinButton: Node) {

    val ui = VBox()

    lateinit var roomfield: StringProperty

    init {
        createui()
    }

    private fun createui() {
        ui.apply {
            hbox {
                label("Room")
                textfield() {
                    roomfield = textProperty()
                    textProperty().addListener({ observable, oldValue, newValue ->
                        joinButton.setDisable(newValue.trim().isEmpty())
                    })
                }
            }
            val roomlist = RoomListView(publicRoomList)
            roomlist.root.selectionModel.selectedItemProperty().toObservable()
                    .filterNotNull() // when nothing's selected
                    .subscribe {
                        roomfield.set(it.room_id)
                    }
            this+=roomlist
        }
    }
}

class RoomListView(roomlist: ObservableList<DiscoveredRoom>): View() {
    override val root = listview(roomlist)

    init {
        with(root) {
            minWidth = 300.0
            cellFragment(DiscoveredRoomFragment::class)
        }
    }
}
