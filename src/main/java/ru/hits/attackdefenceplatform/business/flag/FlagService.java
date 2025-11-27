package ru.hits.attackdefenceplatform.business.flag;

import ru.hits.attackdefenceplatform.business.user.repository.UserEntity;

public interface FlagService {
    void sendFlag(String flag, UserEntity user);
}
