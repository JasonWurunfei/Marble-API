package io.devnoob.marble.api;

import java.io.Console;
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

import io.devnoob.marble.persistence.entity.Marble;
import io.devnoob.marble.persistence.repo.MarbleRepository;

@RestController
@RequestMapping("/api/marble")
public class MarbleController {
    
    @Autowired
    MarbleRepository marbleRepository;

    @PostConstruct
    public void init() {
        marbleRepository.connect();
        if (marbleRepository.findAll().size() == 0) {
            marbleRepository.insert(
                new Marble("hello", 1L, new Timestamp(System.currentTimeMillis()),
                 "你好", "我学编程的一个单词")
            );
            marbleRepository.insert(
                new Marble("world", 1L, new Timestamp(System.currentTimeMillis()),
                 "世界", "我学编程的二个单词")
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marble> getMarble(@PathVariable Long id) {
        Marble marble = marbleRepository.find(id);
        if (marble == null) 
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(marble, HttpStatus.OK);
    }

    @GetMapping("/user/{user_id}")
    public List<Marble> getMarblesByUserId(@PathVariable Long user_id) {
        return marbleRepository.getMarblesByUserId(user_id);
    }

    @GetMapping("/latest/{user_id}")
    public List<Marble> getLatestMarblesByUserId(@PathVariable Long user_id, @RequestParam("limit") int limit) {
        return marbleRepository.getLatestMarblesByUserId(user_id, limit);
    }
    
    @PostMapping("/")
    public ResponseEntity<Marble> createMarble(@RequestBody Marble marble) {
        if(marbleRepository.insert(marble))
            return new ResponseEntity<>(marbleRepository.getLatestMarblesByUserId(marble.getUserId(), 1).get(0), HttpStatus.OK);
        else 
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/")
    public boolean updateMarble(@RequestBody Marble marble) {
        return marbleRepository.update(marble);
    }

    @DeleteMapping("/{marble_id}")
    public boolean deleteMarble(@PathVariable Long marble_id) {
        return marbleRepository.delete(marble_id);
    }

    @DeleteMapping("/batchremove/")
    public boolean batchRemove(@RequestBody List<Long> ids) {
        boolean isSuccess = true;
        for(Long id : ids) {
            isSuccess = marbleRepository.delete(id) && isSuccess;
        }
        return isSuccess;
    }

}
