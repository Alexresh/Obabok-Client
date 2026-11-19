package ru.obabok.o_client.client.tweaks;

import com.google.common.collect.EvictingQueue;
import net.minecraft.resources.Identifier;
import ru.obabok.o_client.References;

public class SoundMuteManager {
    private static final EvictingQueue<Identifier> RECENT_SOUNDS = EvictingQueue.create(30);

    public static void onSound(Identifier soundId){
        RECENT_SOUNDS.add(soundId);
    }

    public static EvictingQueue<Identifier> getRecentSounds(){
        return RECENT_SOUNDS;
    }

    public static void mute(Identifier id) {
        //References.getConfig().SoundMuter.sounds.add(id.toString());

    }

    public static void unmute(Identifier id){
        //References.getConfig().SoundMuter.sounds.remove(id.toString());
    }

    public static boolean isMuted(Identifier id) {
        return false;
        //return References.getConfig().SoundMuter.sounds.contains(id.toString());
    }
}
