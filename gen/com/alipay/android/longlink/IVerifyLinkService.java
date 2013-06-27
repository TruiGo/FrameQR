/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/zhangqian/work/workspace/FrameQR/src/com/alipay/android/longlink/IVerifyLinkService.aidl
 */
package com.alipay.android.longlink;
public interface IVerifyLinkService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.alipay.android.longlink.IVerifyLinkService
{
private static final java.lang.String DESCRIPTOR = "com.alipay.android.longlink.IVerifyLinkService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.alipay.android.longlink.IVerifyLinkService interface,
 * generating a proxy if needed.
 */
public static com.alipay.android.longlink.IVerifyLinkService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.alipay.android.longlink.IVerifyLinkService))) {
return ((com.alipay.android.longlink.IVerifyLinkService)iin);
}
return new com.alipay.android.longlink.IVerifyLinkService.Stub.Proxy(obj);
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
case TRANSACTION_initializePushLink:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.initializePushLink(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_closePushLink:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.closePushLink();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registCallback:
{
data.enforceInterface(DESCRIPTOR);
com.alipay.android.longlink.IVerifyClientCallback _arg0;
_arg0 = com.alipay.android.longlink.IVerifyClientCallback.Stub.asInterface(data.readStrongBinder());
this.registCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unRegistCallback:
{
data.enforceInterface(DESCRIPTOR);
com.alipay.android.longlink.IVerifyClientCallback _arg0;
_arg0 = com.alipay.android.longlink.IVerifyClientCallback.Stub.asInterface(data.readStrongBinder());
this.unRegistCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reConnect:
{
data.enforceInterface(DESCRIPTOR);
this.reConnect();
reply.writeNoException();
return true;
}
case TRANSACTION_isSocketConnected:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isSocketConnected();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registSocketNotifer:
{
data.enforceInterface(DESCRIPTOR);
com.alipay.android.longlink.ISocketResultNotifer _arg0;
_arg0 = com.alipay.android.longlink.ISocketResultNotifer.Stub.asInterface(data.readStrongBinder());
this.registSocketNotifer(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unRegistSocketNofiter:
{
data.enforceInterface(DESCRIPTOR);
this.unRegistSocketNofiter();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.alipay.android.longlink.IVerifyLinkService
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
public boolean initializePushLink(int netType, java.lang.String clientDealDesc) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(netType);
_data.writeString(clientDealDesc);
mRemote.transact(Stub.TRANSACTION_initializePushLink, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean closePushLink() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closePushLink, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void registCallback(com.alipay.android.longlink.IVerifyClientCallback callback) throws android.os.RemoteException
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
public void unRegistCallback(com.alipay.android.longlink.IVerifyClientCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unRegistCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void reConnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reConnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isSocketConnected() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isSocketConnected, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void registSocketNotifer(com.alipay.android.longlink.ISocketResultNotifer notifer) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((notifer!=null))?(notifer.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registSocketNotifer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//socket连接结果通知

public void unRegistSocketNofiter() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unRegistSocketNofiter, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_initializePushLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_closePushLink = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unRegistCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_reConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_isSocketConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_registSocketNotifer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_unRegistSocketNofiter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public boolean initializePushLink(int netType, java.lang.String clientDealDesc) throws android.os.RemoteException;
public boolean closePushLink() throws android.os.RemoteException;
public void registCallback(com.alipay.android.longlink.IVerifyClientCallback callback) throws android.os.RemoteException;
public void unRegistCallback(com.alipay.android.longlink.IVerifyClientCallback callback) throws android.os.RemoteException;
public void reConnect() throws android.os.RemoteException;
public boolean isSocketConnected() throws android.os.RemoteException;
public void registSocketNotifer(com.alipay.android.longlink.ISocketResultNotifer notifer) throws android.os.RemoteException;
//socket连接结果通知

public void unRegistSocketNofiter() throws android.os.RemoteException;
}
