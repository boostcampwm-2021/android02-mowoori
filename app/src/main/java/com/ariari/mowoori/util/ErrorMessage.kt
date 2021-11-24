package com.ariari.mowoori.util

enum class ErrorMessage(val message: String) {
    Offline("Client is offline"),
    Uid("uid is null"),
    UserList("userList is null"),
    CurrentGroupId("groupId is null"),
    GroupInfo("groupInfo is null"),
    MissionInfo("missionInfo is null"),
    StampInfo("stampInfo is null"),
    UserInfo("userInfo is null"),
    Path("invalid path"),
    HashKey("invalid hash key"),
    PushKey("Couldn't get push key for posts"),
    DuplicatedGroup("Group Duplication is not allowed"),
    ExistGroupName("group name already exists"),
    ExistUserName("user name already exists")
}
