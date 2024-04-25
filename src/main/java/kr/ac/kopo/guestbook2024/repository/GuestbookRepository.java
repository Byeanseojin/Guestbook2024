package kr.ac.kopo.guestbook2024.repository;

import kr.ac.kopo.guestbook2024.entity.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestbookRepository extends JpaRepository<GuestBook, Long> {
}
