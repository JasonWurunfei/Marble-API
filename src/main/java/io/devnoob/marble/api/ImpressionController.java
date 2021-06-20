package io.devnoob.marble.api;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.devnoob.marble.persistence.entity.Impression;
import io.devnoob.marble.persistence.repo.ImpressionRepository;

@RestController
@RequestMapping("/api/impression")
public class ImpressionController {
    
    @Autowired
    ImpressionRepository impressionRepository;

    @PostConstruct
    public void init() {
        impressionRepository.connect();
        if(impressionRepository.findAll().size() == 0) {
            impressionRepository.insert(new Impression("/static/video/hello", 1L, 1));
            impressionRepository.insert(new Impression("/static/picture/hello", 1L, 2));
        }
    }

    @GetMapping("/marble/{marble_id}")
    public List<Impression> getImpressionsByMarbleId(@PathVariable Long marble_id) {
        return impressionRepository.getImpressionsByMarbleId(marble_id);
    }

    @DeleteMapping("{impression_id}")
    public boolean deleteImpression(@PathVariable Long impression_id) {
        return impressionRepository.delete(impression_id);
    }

    @DeleteMapping("/batchremove")
    public boolean batchRemove(@RequestBody List<Long> ids) {
        boolean isSuccess = true;
        for(Long id : ids){
            isSuccess = impressionRepository.delete(id) && isSuccess;
        }
        return isSuccess;
    }
}
