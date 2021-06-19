package io.devnoob.marble.api;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.devnoob.marble.persistence.entity.Bag;
import io.devnoob.marble.persistence.repo.BagRepository;

@RestController
@RequestMapping("/api/bag")
public class BagController {
    
    @Autowired
    BagRepository bagRepository;

    @PostConstruct
    public void init() {
        bagRepository.connect();
        if (bagRepository.findAll().size() == 0) {
            bagRepository.insert(new Bag(1L, "test_bag1", new Timestamp(1623917398)));
            bagRepository.insert(new Bag(2L, "test_bag2", new Timestamp(1623917480)));
        }
    }

    @GetMapping("/marble/{id}")
    public ResponseEntity<Bag> getBag(@PathVariable Long id) {
        Bag bag = bagRepository.find(id);
        if (bag == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(bag, HttpStatus.OK);
    }

    @GetMapping("/user/{user_id}")
    public List<Bag> getBagsByUserId(@PathVariable Long user_id) {
        return bagRepository.getBagsByUserId(user_id);
    }

    @PostMapping("/")
    public boolean createUser(@RequestBody Bag bag) {
        return bagRepository.insert(bag);
    }

    @PutMapping("/")
    public boolean updateMarble(@RequestBody Bag bag) {
        return bagRepository.update(bag);
    }

    @DeleteMapping("/{bag_id}")
    public boolean deleteMarble(@PathVariable Long bag_id) {
        return bagRepository.delete(bag_id);
    }

    @DeleteMapping("/batchremove/")
    public boolean batchRemove(@RequestBody List<Long> ids) {
        boolean isSuccess = true;
        for(Long id : ids) {
            isSuccess = bagRepository.delete(id) && isSuccess;
        }
        return isSuccess;
    }
}
