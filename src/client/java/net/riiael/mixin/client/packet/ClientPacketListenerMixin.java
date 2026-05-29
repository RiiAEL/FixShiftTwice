package net.riiael.mixin.client.packet;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Objects;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl
{
    @Shadow
    private ClientLevel level;

    protected ClientPacketListenerMixin()
    {
        super(null, null, null);
    }

    @Redirect(method = "handleSetEntityData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;packedItems()Ljava/util/List;"))
    public List<SynchedEntityData.DataValue<?>> res(ClientboundSetEntityDataPacket packet)
    {
        if (Objects.equals(level.getEntity(packet.id()), minecraft.player))
        {
            packet.packedItems().removeIf(dataValue -> dataValue.serializer().equals(EntityDataSerializers.POSE));
        }
        return packet.packedItems();
    }


}
