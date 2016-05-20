package fun.wxy.annoy_o_tron.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fun.wxy.annoy_o_tron.service.OverlayService;

public class OverlayReceiver extends BroadcastReceiver {
    public OverlayReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("========= i receive order =============");
        OverlayService.instance.imageView.setBackgroundColor(0x330000ff);
        System.out.println("------ DONE ----------");
    }
}
