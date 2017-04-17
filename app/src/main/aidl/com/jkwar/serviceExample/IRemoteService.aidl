// IRemoteService.aidl
package com.jkwar.serviceExample;
import com.jkwar.serviceExample.Person;
import com.jkwar.serviceExample.IParticipateCallback;
// Declare any non-default types here with import statements

interface IRemoteService {
          //求和
          int add(int a, int b);
          //转化
          String toUpperCase(String str);
          //保存
          Person save(in Person person);
          //添加用户
          void join(IBinder token,String userName);
          //删除用户
          void leave(IBinder token);
          //用户列表
          List<String> getParticipators();
          //加入
          void registerParticipateCallback(IParticipateCallback cb);
          //离开
          void unregisterParticipateCallback(IParticipateCallback cb);
}
