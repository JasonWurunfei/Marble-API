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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.devnoob.marble.persistence.entity.Bag;
import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.entity.MarbleBag;
import io.devnoob.marble.persistence.repo.BagRepository;
import io.devnoob.marble.persistence.repo.MarbleBagRepository;

@RestController
@RequestMapping("/api/bag")
public class BagController {
    
    @Autowired
    BagRepository bagRepository;

    @Autowired
    MarbleBagRepository marbleBagRepository;

    @PostConstruct
    public void init() {
        bagRepository.connect();
        if (bagRepository.findAll().size() == 0) {
            bagRepository.insert(new Bag(1L, "test_bag1", new Timestamp(1623917398)));
            bagRepository.insert(new Bag(1L, "test_bag2", new Timestamp(1664617398)));
            bagRepository.insert(new Bag(1L, "test_bag3", new Timestamp(1623917298)));
            bagRepository.insert(new Bag(2L, "test_bag4", new Timestamp(1623917480)));
        }

        marbleBagRepository.connect();
        if (marbleBagRepository.findAll().size() == 0) {
            marbleBagRepository.insert(new MarbleBag(1L, 1L, 1L));
            marbleBagRepository.insert(new MarbleBag(2L, 2L, 1L));
            marbleBagRepository.insert(new MarbleBag(3L, 3L, 2L));
            marbleBagRepository.insert(new MarbleBag(4L, 4L, 2L));
            marbleBagRepository.insert(new MarbleBag(5L, 1L, 2L));
        }
    }

    @GetMapping("/user/{user_id}")
    public List<Bag> getBagsByUserId(@PathVariable Long user_id) {
        return bagRepository.getBagsByUserId(user_id);
    }

    @PostMapping("/")
    public boolean createBag(@RequestBody Bag bag) {
        return bagRepository.insert(bag);
    }

    @PutMapping("/")
    public boolean updateBag(@RequestBody Bag bag) {
        return bagRepository.update(bag);
    }

    @DeleteMapping("/{bag_id}")
    public boolean deleteBag(@PathVariable Long bag_id) {
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

    /**
     * Get all marbles that are belonging to the bag with given ID
     * @param BagId
     * @return List<Marble> List of matching marbles if the 
     * given bag ID is valid otherwise, 404.
     */
    @GetMapping("/marbles/{bag_id}")
    public ResponseEntity<List<Marble>> getMarblesByBagId(@PathVariable Long bag_id) {
        if (bagRepository.find(bag_id) == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        List<Marble> marbles = marbleBagRepository.getMarbleByBagId(bag_id);
        return new ResponseEntity<>(marbles, HttpStatus.OK);
    }
}
