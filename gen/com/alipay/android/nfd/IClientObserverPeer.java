/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangqian/work/workspace/FrameQR/src/com/alipay/android/nfd/IClientObserverPeer.aidl
 */
package com.alipay.android.nfd;
/**
 * 参与者和发现者操作接口
 * @author daping.gp
 * @version $Id: ClientObserver.java, v 0.1 2012-6-18 下午3:58:06 daping.gp Exp $
 */
public interface IClientObserverPeer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.alipay.android.nfd.IClientObserverPeer
{
private static final java.lang.String DESCRIPTOR = "com.alipay.android.nfd.IClientObserverPeer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.alipay.android.nfd.IClientObserverPeer interface,
 * generating a proxy if needed.
 */
public static com.alipay.android.nfd.IClientObserverPeer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.alipay.android.nfd.IClientObserverPeer))) {
return ((com.alipay.android.nfd.IClientObserverPeer)iin);
}
return new com.alipay.android.nfd.IClientObserverPeer.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_unRegist:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.unRegist();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_discoverClientInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.discoverClientInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registCallback:
{
data.enforceInterface(DESCRIPTOR);
com.alipay.android.nfd.IClientCallback _arg0;
_arg0 = com.alipay.android.nfd.IClientCallback.Stub.asInterface(data.readStrongBinder());
this.registCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isDiscovering:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isDiscovering();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_encryptRegist:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.encryptRegist(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_modifyBluetoothName:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.modifyBluetoothName();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_businessExit:
{
data.enforceInterface(DESCRIPTOR);
this.businessExit();
reply.writeNoException();
return true;
}
case TRANSACTION_hasRegisted:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.hasRegisted();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isDiscoverTimeout:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isDiscoverTimeout();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.alipay.android.nfd.IClientObserverPeer
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * 解除注册信息
     * @return
     */
public boolean unRegist() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unRegist, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * 发现者获取参与者list信息
     * @return
     */
public void discoverClientInfo(java.lang.String bizType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(bizType);
mRemote.transact(Stub.TRANSACTION_discoverClientInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * 注册回调通知函数
     * @param callback
     */
public void registCallback(com.alipay.android.nfd.IClientCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isDiscovering() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isDiscovering, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void encryptRegist(java.lang.String reginfo, java.lang.String param, java.lang.String bizType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(reginfo);
_data.writeString(param);
_data.writeString(bizType);
mRemote.transact(Stub.TRANSACTION_encryptRegist, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean modifyBluetoothName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_modifyBluetoothName, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void businessExit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_businessExit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean hasRegisted() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_hasRegisted, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isDiscoverTimeout() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isDiscoverTimeout, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_unRegist = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_discoverClientInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_isDiscovering = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_encryptRegist = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_modifyBluetoothName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_businessExit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_hasRegisted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_isDiscoverTimeout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
/**
     * 解除注册信息
     * @return
     */
public boolean unRegist() throws android.os.RemoteException;
/**
     * 发现者获取参与者list信息
     * @return
     */
public void discoverClientInfo(java.lang.String bizType) throws android.os.RemoteException;
/**
     * 注册回调通知函数
     * @param callback
     */
public void registCallback(com.alipay.android.nfd.IClientCallback callback) throws android.os.RemoteException;
public boolean isDiscovering() throws android.os.RemoteException;
public void encryptRegist(java.lang.String reginfo, java.lang.String param, java.lang.String bizType) throws android.os.RemoteException;
public boolean modifyBluetoothName() throws android.os.RemoteException;
public void businessExit() throws android.os.RemoteException;
public boolean hasRegisted() throws android.os.RemoteException;
public boolean isDiscoverTimeout() throws android.os.RemoteException;
}
