package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.TutorSubject;
import za.ac.cput.domain.TutorSubjectId;

@Repository
public interface TutorSubjectRepository extends JpaRepository<TutorSubject, TutorSubjectId> {
}
