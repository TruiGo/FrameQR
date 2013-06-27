/**
 * 命令处理和分发
 */
package com.alipay.platform.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alipay.android.net.NetworkManager;

/**
 * @author sanping.li@alipay.com
 *
 */
public class CommandHandler extends Handler {
    private Controller mController;

    public CommandHandler(Looper looper, Controller controller) {
        super(looper);
        this.mController = controller;
    }

    @Override
    public void handleMessage(Message msg) {
        Command command = (Command) msg.obj;
        int action = command.getAction();
        byte read = (byte) ((action >> Command.ACT_READ) & 0xff);
        byte write = (byte) ((action >> Command.ACT_SAVE) & 0xff);
        byte delete = (byte) ((action >> Command.ACT_DELETE) & 0xff);

        handleRead(command, read);

        handleWrite(command, write);

        handleDelete(command, delete);
    }

    /**
     * @param command
     * @param delete
     */
    private void handleDelete(Command command, byte delete) {

        if ((delete & Module.CTR_NET) == Module.CTR_NET) {//直接失败
            command.setState(null, Command.STATE_FAILED);
        }

    }

    /**
     * @param command
     * @param write
     */
    private void handleWrite(Command command, byte write) {

        if ((write & Module.CTR_NET) == Module.CTR_NET) {
            NetworkManager networkManager = mController.getNetworkManager();
            command.setState(networkManager, Command.STATE_STARTED);
            networkManager.connect(command);
        }
    }

    /**
     * @param command
     * @param read
     */
    private void handleRead(Command command, byte read) {

        if (command.getResponseData() == null && (read & Module.CTR_NET) == Module.CTR_NET) {
            NetworkManager networkManager = mController.getNetworkManager();
            command.setState(networkManager, Command.STATE_STARTED);
            networkManager.connect(command);
        }
    }

}
