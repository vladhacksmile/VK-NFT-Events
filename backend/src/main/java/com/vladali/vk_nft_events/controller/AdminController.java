package com.vladali.vk_nft_events.controller;

import com.vladali.vk_nft_events.dto.*;
import com.vladali.vk_nft_events.model.*;
import com.vladali.vk_nft_events.service.EventsService;
import com.vladali.vk_nft_events.service.UserEventService;
import com.vladali.vk_nft_events.service.Web3Service;
import com.vladali.vk_nft_events.util.FileUploadUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequestMapping(value = "/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Value("${contract}")
    String contract;

    @Autowired
    Web3Service web3Service;
    @Autowired
    EventsService eventsService;

    @Autowired
    UserEventService userEventService;

    @PostMapping(value = "createContract")
    public ResponseEntity<?> createContract(@ModelAttribute EventDTO eventDTO) throws Exception {
        MultipartFile multipartFile = eventDTO.getFile();
        System.out.println(multipartFile.getName());

        String wallet = eventDTO.getWallet_address();
//        String contract = web3Service.generateContract(wallet);
        String fileName = multipartFile.getOriginalFilename();
        String fileCode = FileUploadUtil.saveFile(fileName, multipartFile);

        String jsonFilePath = web3Service.formJsonFile(fileCode, eventDTO);
        String jsonIpfsUri = web3Service.getIpfsUri(jsonFilePath);

        Nfts nft = web3Service.mint(eventDTO.getWallet_address(), jsonIpfsUri, contract);
        Events events = eventsService.add(eventDTO, nft);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping(value = "getWhiteList")
    public ResponseEntity<?> getWhiteList() {
        return new ResponseEntity<>("Event was created successfully!", HttpStatus.ACCEPTED);
    }

    @GetMapping("/group/{id}")
    public @ResponseBody Iterable<Events> getEventsByGroupId(@PathVariable Long id) {
        return eventsService.getGroupEvents(id);
    }

    @GetMapping(value = "/generate/{id}")
    public ResponseEntity<?> genToken(@PathVariable long id) {
        Events events = eventsService.getEventById(id);
        if(events != null) {
            TokenDTO tokenDTO = new TokenDTO(createToken(events), "");
            return new ResponseEntity<>(tokenDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Access denied!", HttpStatus.LOCKED);
        }
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<?> validate(@RequestBody TokenDTO token) {
        System.out.printf(token.getToken());
        if(validateToken(token.getToken())) {
            Events events = eventsService.getEventById(getEventIdFromToken(token.getToken()));
            if(events != null) {
                return new ResponseEntity<>(events, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Cheats!", HttpStatus.LOCKED);
    }

    @GetMapping(value = "getWhiteListRules", params = "id")
    public ResponseEntity<?> getEventResponsibilities(@RequestParam Long id) {
        // here must be code that returns rules that allows to be in white list (persons who can get NFT)
        return new ResponseEntity<>("Event was created successfully!", HttpStatus.ACCEPTED);
    }

    // Токен для QR-а
    public String createToken(Events events) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 60000);

        return Jwts.builder()
                .setSubject(Long.toString(events.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, "vknfteventssecretkey")
                .compact();
    }

    public Long getEventIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey("vknfteventssecretkey")
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey("vknfteventssecretkey").parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            System.err.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.err.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.err.println("JWT claims string is empty.");
        }
        return false;
    }

    @PostMapping(value = "createNft")
    public ResponseEntity<?> createNft(@RequestBody UserNftDto userNftDto)  {
        String wallet = userNftDto.getWallet_address();
        System.out.println(userNftDto);
        Events event = eventsService.getEventById(userNftDto.getEventId());
        Nfts nft = web3Service.mint(wallet, event.getDataUri(), event.getSmartContracts());
        UsersEvents usersEvents = userEventService.addUserEvent(userNftDto, nft.getTokenId());
        return new ResponseEntity<>(usersEvents, HttpStatus.OK);
    }

    @GetMapping("user/{id}/events")
    public @ResponseBody Iterable<UsersEvents> getEventsForUser(@PathVariable Long id) {
        return userEventService.getEventsForUser(id);
    }



//    TODO make endpoint for get usernfts by event id
//    TODO выводи полную инфу по nft в методе user/{id}/events
//    TODO slf4j
//    TODO exception handling and validation
//    TODO delegate bussines logic to component
//    TODO docker-compose
//    TODO github actions

}
