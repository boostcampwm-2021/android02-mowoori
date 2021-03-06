package com.ariari.mowoori.util

enum class InvalidMode(val message: String) {
    AlreadyJoin("이미 해당 그룹의 구성원입니다!"),
    InValidCode("유효하지 않은 코드입니다!"),
    InValidGroupName("1자 이상 11자 이하로 입력해주세요!"),
    AlreadyExistGroupName("이미 존재하는 그룹 이름입니다!"),
    AlreadyExistNickname("이미 존재하는 닉네임입니다!"),
    InvalidNickname("1자 이상 10자 이하로 입력해주세요!")
}
