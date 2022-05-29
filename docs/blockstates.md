# Unused Block States
!!! note
    This page is purely to document all unused block states in Minecraft, 
    which might be useful to explain PolyMc's behaviour or might help if you're doing some serverside hackery yourself.

## What to pay attention to
 * Render layer
 * Collision box
 * Selection box
 * Whether the state is waterlogged or not

All of these blocks are unused on the client. This means that different states don't have different textures. 
The properties are probably still used on the server. So it's important that you can still differentiate these on the server.
(PolyMc does this by only working in the packet layer, none of the blocks inside the world are actually touched, they're just regular (modded) blocks).

!!! note
    The number of blocks here usually represents the amount of states unless noted otherwise with a *.

!!! warning
    This table has a bunch of information, I have no doubt made a mistake somewhere in here. If you notice any, or if there's an entry missing, please let me know.

| Block                    | # of blocks | Used in PolyMc | Renderlayer         | Notes                                                                                                                                                                                                                                                                                                                                                                       |
|--------------------------|-------------|----------------|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Noteblocks               | 799         | >yes<          | standard            | Note blocks only require a small bit of code to make sure the particles still work. All of their properties are only needed on the server, the client will still play the correct note even if it doesn't know the states. Needs resynchronizing when the block below changes.                                                                                              |
| Beenest/Beehive          | 24          | >yes<          | standard            | Beehives/nests only have a different texture for no honey and full honey.                                                                                                                                                                                                                                                                                                   |
| Target block             | 15          | >yes<          | standard            | The block needs resynchronization when hit with an arrow.                                                                                                                                                                                                                                                                                                                   |
| Dispenser/dropper        | 12          | >yes<          | standard            | The client doesn't need to know whether the block is powered or not.                                                                                                                                                                                                                                                                                                        |
| Infested blocks          | 9           | >yes<          | standard            | Infested blocks look the same as their un-infested counterparts.                                                                                                                                                                                                                                                                                                            |
| Copper blocks            | 4           | >yes<          | standard            | Waxed copper blocks can be replaced with their unwaxed block states.                                                                                                                                                                                                                                                                                                        |
| Snowy mycelium/podzol    | 2           | >yes<          | standard            | Snowy mycelium and podzol display the same as a snowy grass block                                                                                                                                                                                                                                                                                                           |
| Jukebox                  | 1           | >yes<          | standard            | There's a `has_record` property that the client doesn't need to care about.                                                                                                                                                                                                                                                                                                 |
| Tnt                      | 1           | >no[^3]<       | standard            | Tnt has an unstable property that the client doesn't need to care about. Note that tnt is instantly breakable, which might cause trouble if you're doing server-side block breaking.                                                                                                                                                                                        |
| Double slabs             | 51          | >yes<          | standard            | Double slabs have the same texture as their full block counterpart. The smooth stone double slab is the only exception to this.                                                                                                                                                                                                                                             |
| Waterlogged double slabs | 52          | >yes<          | standard            | These states are completely unused. Note that they will create water drip particles.                                                                                                                                                                                                                                                                                        |
| Petrified oak slab       | 1*          | >yes<          | standard            | Unlike the double slab states, this one can be used for actual slabs instead of just full blocks.                                                                                                                                                                                                                                                                           |
| Waxed copper slab        | 4*          | >yes<          | standard            | Unlike the double slab states, this one can be used for actual slabs instead of just full blocks. Waxed copper blocks can be replaced with their unwaxed block states. The "# of blocks" represents the number of slabs you can make.                                                                                                                                       |
| Sculk sensors            | 30          | >yes<          | cut-out             | Sculk sensors have a power propery that the client doesn't need to care about. Note that the "active" states produce particles and are emmisive. Sculk sensors have the same collisions as a lower half slab.                                                                                                                                                               |
| Leaves                   | 111         | >no<           | cut-out mipped      | Leaves have the advantage of not culling the blocks around them. All of their properties are not needed by the client.                                                                                                                                                                                                                                                      |
| Kelp                     | 25          | >yes<          | cut-out             | All of the states are waterlogged.                                                                                                                                                                                                                                                                                                                                          |
| Saplings                 | 6           | >yes<          | cut-out             | Saplings are blocks without collisions that have a large selection box. Their stage is only needed on the server.                                                                                                                                                                                                                                                           |
| Sugarcane                | 15          | >yes<          | cut-out             | Is coloured depending on the biome.                                                                                                                                                                                                                                                                                                                                         |
| Small dripleaf           | 3           | >yes<          | cut-out             | The lower part of the dripleaf doesn't care about direction.                                                                                                                                                                                                                                                                                                                |
| Tripwire                 | 96          | >yes<          | translucent (kinda) | The client doesn't need to know if string is powered or detached. Needs resynchronizing if the block next to it changes and if that would change the string's state.                                                                                                                                                                                                        |
| Tripwire hooks           | 4           | >no<           | cut-out mipped      | The powered but not attached property is unused (TODO: confirm this). The block does have an unusual selection box.                                                                                                                                                                                                                                                         |
| Cave vines               | 50          | >no[^2]<       | cut-out             | The age isn't needed on the client. Note that this block is climbable.                                                                                                                                                                                                                                                                                                      |
| Twisting/weeping vines   | 50          | >no[^6]<       | cut-out             | These vines have an age property that the client doesn't need to care about. Note that the blocks are climbable.                                                                                                                                                                                                                                                            |
| Plants                   | ?           | >no[^5]<       | cut-out             | Certain plants (carrots, nether wart, etc) have certain age levels that don't have a separate texture.                                                                                                                                                                                                                                                                      |
| Buttons                  | 32          | >no<           | standard            | Buttons facing north/south or east/west are the same when placed on the floor/ceiling.                                                                                                                                                                                                                                                                                      |
| Fence gate               | 128         | >yes<          | standard            | The client does not need to know whether the fence gate is powered or not. There are also some duplicate states for some directions. 64 of these states are open, making them usable as a block without collision.                                                                                                                                                          |
| Doors                    | 27*         | >kinda[^1]<    | cut-out             | The client does not need to know whether the door is powered or not. There are also some duplicate states, an open door to the north can be represented by a closed door to the west. Note that iron doors will behave differently. The "# of blocks" represents the amount of custom doors you can make here, as it doesn't make sense to list the amount of block states. |
| Trapdoors                | 8*          | >yes<          | cut-out             | The client does not need to know whether the trapdoor is powered or not. Note that iron trapdoors will behave differently. The "# of blocks" represents the amount of custom trapdoors you can make here, as it doesn't make sense to list the amount of block states.                                                                                                      |
| Waxed copper stairs      | 4*          | >yes<          | standard            | Waxed copper blocks can be replaced with their unwaxed block states. The "# of blocks" represents the number of stairs you can make.                                                                                                                                                                                                                                        |
| Weighted pressure plates | 28          | >no[^5]<       | standard            | There's a power level from 0-15, but there's only a texture for powered and unpowered.                                                                                                                                                                                                                                                                                      |
| Farmland                 | 6           | >yes<          | standard            | Farmland has a collision box that makes it useful for paths, they're just lower than a full block. There's a moisture level from 0-7, but there's only a texture for unmoistured and moistured.                                                                                                                                                                             |
| Bamboo                   | 1*          | >no[^5]<       | cut-out             | Bamboo has an age level that the client doesn't need to care about. This allows for 1 custom bamboo block.                                                                                                                                                                                                                                                                  |
| Cactus                   | 15          | >yes<          | cut-out             | Cacti have an age property that the client doesn't need to care about. They have an odd collision box though.                                                                                                                                                                                                                                                               |
| Daylight detectors       | 30          | >no[^5]<       | standard            | Daylight detectors have a powered property that the client doesn't need to care about.                                                                                                                                                                                                                                                                                      |
| End portal frame         | 4           | >no[^5]<       | standard            | End portal frames orientated north/south and east/west look the same.                                                                                                                                                                                                                                                                                                       |
| Scaffolding              | 28          | >no[^5]<       | cut-out             | Scaffolding has a distance property that the client doesn't need to care about. Scaffolding does have weird movement properties and is climbable. Half of the states are waterlogged.                                                                                                                                                                                       |
| Redstone                 | 8           | >no[^5]<       | cut-out             | Redstone has some unused states pointing in only one direction. Unfortunately power levels above 0 can't be used due to particles. They also have a weird selection box and are instantly breakable. Not a great block to work with.                                                                                                                                        |
| Hopper                   | 1*          | >no[^5]<       | standard            | The client doesn't have to know whether the hopper is powered or not. This allows for 1 custom hopper.                                                                                                                                                                                                                                                                      |
| Bell                     | 1*          | >no[^5]<       | standard            | The bell has a powered property that the client doesn't need to care about. This allows for 1 custom bell. Note that the bell itself is rendered with a ber.                                                                                                                                                                                                                |
| Lectern                  | 1*          | >no[^5]<       | standard            | The lectern has a powered property that the client doesn't need to care about. This allows for 1 custom lectern.                                                                                                                                                                                                                                                            |
| Stairs                   | ?           | >no<           | standard            | Stairs technically have some duplicate states, for example a stair facing north with a connection on the left is the same as a stair facing east with a connection on the right. They're not that useful.                                                                                                                                                                   |
| Walls                    | 21          | >no[^4]<       | standard            | Walls have a bunch of weird states, including one without selection box and collision box (like air). I'm sure they're useful for some specific cases but they're a very odd block. The "# of blocks" represents the amount of different wall variants.                                                                                                                     |
| Beds                     | 32          | >no[^5]<       | cut-out             | Beds have an occupied property that the client doesn't care about. Note that beds are implemented using block entity renderers so you can only really add to the block.                                                                                                                                                                                                     |

[^1]: Only the powered state is currently implemented.
[^2]: Currently disabled due to issues with climbable states. See [#146](https://github.com/TheEpicBlock/PolyMc/issues/146).
[^3]: Currently disabled due to issues with tnt being instantly breakable.
[^4]: Only implemented for the air-like case. There are others, but they're a very odd shape.
[^5]: Tracked in [#145](https://github.com/TheEpicBlock/PolyMc/issues/145).
[^6]: Unimplemented until I figure out what to do with climbable states. See [#146](https://github.com/TheEpicBlock/PolyMc/issues/146).