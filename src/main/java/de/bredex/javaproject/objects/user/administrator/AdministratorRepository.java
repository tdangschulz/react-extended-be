package de.bredex.javaproject.objects.user.administrator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource()
@CrossOrigin("http://localhost:5173/")
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {

}
