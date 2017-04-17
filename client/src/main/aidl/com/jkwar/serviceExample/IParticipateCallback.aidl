// IParticipateCallback.aidl
package com.jkwar.serviceExample;

// Declare any non-default types here with import statements

interface IParticipateCallback {
        // 用户加入或者离开的回调
        void onParticipate(String name, boolean joinOrLeave);
}
