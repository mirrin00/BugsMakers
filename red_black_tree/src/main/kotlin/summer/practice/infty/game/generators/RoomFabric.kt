package summer.practice.infty.game.generators

import summer.practice.infty.game.rooms.Room

internal interface RoomFabric{
    fun generateRoom(depth_level: Int): Room
}