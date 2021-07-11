package summer.practice.infty.game.generators

import summer.practice.infty.game.Attributes
import summer.practice.infty.game.Element
import summer.practice.infty.game.Game
import summer.practice.infty.game.items.ItemType
import kotlin.math.pow
import kotlin.random.Random

object Generator{
    private val room_fabric: RoomFabric = GameRoomFabric()
    private val creature_fabric: CreatureFabric = GameCreatureFabric()
    private val room_event_fabric: RoomEventFabric = UsualRoomEventFabric()
    private val item_fabric: ItemFabric = GameItemFabric()

    fun generateRoom(depth_level: Int) = room_fabric.generateRoom(depth_level)

    fun generateCreature(depth_level: Int, element: Element) = creature_fabric.generateCreature(depth_level, element)

    fun generateRoomEvent(depth_level: Int) = room_event_fabric.generateRoomEvent(depth_level)

    fun generateItem(depth_level: Int) = item_fabric.generateItem(depth_level)

    fun getRandomFromDeep(depth_level: Int) = getRatioFromDeep(depth_level) * Random.nextDouble(0.5, 2.5)

    fun generateElement(red: Boolean? = null): Element{
        if(red == null){
            return when((0..7).random()){
                0 -> Element.USUAL
                1 -> Element.HELLISH
                2 -> Element.MARINE
                3 -> Element.FROSTY
                4 -> Element.SANDY
                5 -> Element.UNDERGROUND
                6 -> Element.HEAVENLY
                7 -> Element.NONE
                else -> Element.NONE
            }
        }else{
            return if(red) when((0..2).random()){
                0 -> Element.HELLISH
                1 -> Element.SANDY
                2 -> Element.UNDERGROUND
                else -> Element.HELLISH
            } else when((0..3).random()){
                0 -> Element.USUAL
                1 -> Element.MARINE
                2 -> Element.HEAVENLY
                3 -> Element.FROSTY
                else -> Element.USUAL
            }
        }
    }

    fun generateItemType(): ItemType = when((0 until ItemType.values().size - 1).random()){
        0 -> ItemType.ARMOR
        1 -> ItemType.WEAPON
        2 -> ItemType.MAGIC
        3 -> ItemType.AMULET
        4 -> ItemType.MAGIC_POTION
        5 -> ItemType.HEALTH_POTION
        6 -> ItemType.OTHER
        else -> ItemType.OTHER
    }

    fun generateAttribute(): Attributes = when((0 until Attributes.values().size).random()){
        0 -> Attributes.DEXTERITY
        1 -> Attributes.STRENGTH
        2 -> Attributes.INTELLIGENCE
        3 -> Attributes.PERCEPTION
        4 -> Attributes.NONE
        else -> Attributes.NONE
    }

    private fun getRatioFromDeep(local_deep: Int): Double{
        val base = 1.5
        val ratio = 2.75
        return ratio * base.pow(local_deep.toDouble())
    }
}