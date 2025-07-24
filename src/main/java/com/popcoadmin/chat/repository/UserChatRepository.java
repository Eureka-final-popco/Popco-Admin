package com.popcoadmin.chat.repository;

import com.popcoadmin.chat.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserChatRepository extends JpaRepository<UserChat, Long> {
}
