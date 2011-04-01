package com.my2do.repo;


import com.my2do.idm.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Needs to be in Java cuz Spring does not create the right proxy if this is a trait
 * User: warren
 * Date: 3/23/11
 * Time: 4:53 PM
 */

@Transactional(readOnly = true)
public interface ResourceRepository extends JpaRepository<Resource,Long>{
}
