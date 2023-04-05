package com.vladali.vk_nft_events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladali.vk_nft_events.contracts.VKNFTHackathon;
import com.vladali.vk_nft_events.dto.EventDTO;
import com.vladali.vk_nft_events.model.Nfts;
import com.vladali.vk_nft_events.util.FileUploadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

@Slf4j

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Web3Service {


    public String generateContract(String wallet) throws Exception {
        return deployContract(web3, credentials);
    }


    @Value("${wallet.privatekey}")
    private String privatekey;

    @Value("${wallet.gaslimit.mint}")
    private Integer gasLimitInt;

    @Value("${ipfs.credentials}")
    private String ipfsPrivateKey;

    @Value("${sepolia.http.provider}")
    private String nftHttpProvider;

    @Value("${ipfsUrl}")
    private String ipfsUrl;




    Web3j web3;
    Credentials credentials;
    BigInteger gasLimit;

    @PostConstruct
    public void postConstruct() {
        web3 = Web3j.build(new HttpService(nftHttpProvider));
        credentials = Credentials.create(privatekey);
    }

    public ECKeyPair createNewKey() throws Exception {
        try {
            return Keys.createEcKeyPair();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("Error Creating New Ec Key Pair");
            log.error("Cannot create Ec key pair", e);
            throw new Exception("Error Creating new key");
        }
    }

    /**
     * Recommended Gas price from Etherscan in Gwei (see https://etherscan.io/gastracker)
     *
     * @return Recommended Gas price in Gwei
     */
    public long getRecommendedGaspriceInGwei() throws Exception {
//        try {
//            JsonNode rootNode = (new ObjectMapper()).readTree(new URL("https://api.etherscan.io/api?module=gastracker&action=gasoracle&apikey=XVY8HG4U4PR2EGX3UPRYBYVXXBJ4M4XHW6").openStream());
//            int gasPrice = rootNode.path("result").path("ProposeGasPrice").asInt();
//            System.out.println("С АПИ:" + String.valueOf(gasPrice + 2));
//            if (gasPrice < 0) {
//                throw new Exception();
//            }
//            return (gasPrice + 2);
//        } catch (Exception e) {
//            System.err.println("залупа какая-то с газпрайсом");
//        }
        return 20L;
    }

    private VKNFTHackathon getContract(String contract) throws Exception {
        return getContract(credentials, contract);
    }

    private VKNFTHackathon getContract(Credentials credentials, String contract) throws Exception {
        BigInteger gasPriceInWei = BigInteger.valueOf(getRecommendedGaspriceInGwei() * 1_000_000_000L);
        this.gasLimit = BigInteger.valueOf(gasLimitInt);
        final ContractGasProvider gasProvider = new StaticGasProvider(gasPriceInWei, gasLimit);
        return VKNFTHackathon.load(contract, web3, credentials, gasProvider);
    }

    public Nfts mint(String address, String uri, String smartContracts) {
        try {

            VKNFTHackathon contract = getContract(smartContracts);

            //update gas provider
            long gasPriceInWei = getRecommendedGaspriceInGwei() * 1_000_000_000L;
            System.out.println(getCurrentMintingCost());

            BigInteger gasPrice = new BigInteger("36000000000"); // 5 GWei
            BigInteger gasLimit = new BigInteger("300000"); // Custom gas limit

            final ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            contract.setGasProvider(gasProvider);

            //Do the deed
            TransactionReceipt transactionReceipt = contract.safeMint(address, uri).send(); //blocking call
            BigInteger tokenId = contract.getTransferEvents(transactionReceipt).get(0).tokenId;

            Nfts nft = new Nfts();
            nft.setDataUri(uri);
            nft.setSmartContracts(smartContracts);
            nft.setTokenId(Long.valueOf(String.valueOf(tokenId)));

            return nft;


        } catch (Exception e) {
            log.error("Error while minting [address: " + address + "]", e);
        }

        return null;

    }


    public String formJsonFile(String filePath, EventDTO event) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FileUploadUtil.IpfsJsonFile jsonFile= new FileUploadUtil.IpfsJsonFile();
        jsonFile.setName(event.getName());
        jsonFile.setDescription(event.getDescription());
        jsonFile.setImage(getIpfsUri(filePath));
        List<FileUploadUtil.IpfsJsonFile.Attributes> attributes = new ArrayList<>();
        attributes.add(new FileUploadUtil.IpfsJsonFile.Attributes("time", event.getTime()));
        jsonFile.setAttributes(attributes);
        File file = new File(filePath+".json");
        FileWriter json = new FileWriter(file);

        objectMapper.writeValue(json,jsonFile);
        return file.getCanonicalPath();
    }

    public String getIpfsUri(String filePath) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("2Lm52vov3J7fzv3sgM3ghXg68hH", "d2d162568ee283089ab223fb5cec94d5");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        Resource file2 = new FileSystemResource(filePath);
        multipartBodyBuilder.part("file", file2);

        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(multipartBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
                .postForEntity(ipfsUrl, requestEntity, String.class);
        response.getBody();
        String hash = response.getBody().split(",")[1];
        hash = hash.split(":")[1];
        hash = hash.substring(1, hash.lastIndexOf("\""));
        return "ipfs://" + hash;
    }


    public String getCurrentOwner(long token, String contractName) {
        VKNFTHackathon contract = null;
        try {
            contract = getContract(contractName);
            return contract.ownerOf(BigInteger.valueOf(token)).send();
        } catch (Exception e) {
            log.error("Cannot get current owner");
            log.error("cannot get current owner", e);
        }
        return null;
    }

    /**
     * @return current price of minting in us cents
     */
    public int getCurrentMintingCost() throws Exception {
        /**
         * 1 Gwei = 10^9 Wei
         * 1 Ether = 10^9 Gwei
         */

        //TODO: change pricing here
        long gascost = 180_000L; //gas cost for minting, in gwei
        long gasprice = getRecommendedGaspriceInGwei(); //in gwei
        long gas = gascost * gasprice; // in gwei
        long eth_in_usd_cents = 269000; //price of single ether = 1_000_000_000 gwei in usd cents
        long cost = gas * eth_in_usd_cents / 1_000_000_000L;
        final int steps = 143; //in cents
        int finalcost = (int) (Math.ceil(((double) cost + 200d) / ((double) steps)) * steps);
        return finalcost;
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
        return VKNFTHackathon.deploy(web3j, credentials, BigInteger.valueOf(getRecommendedGaspriceInGwei()), BigInteger.valueOf(gasLimitInt))
                .send()
                .getContractAddress();
    }

    public TransferResponse transfer(String fromPrivateKey, String toAddress, long token,String contractName) {
        VKNFTHackathon contract = null;
        TransferResponse response = new TransferResponse();
        try {
            Credentials fromCredentials = Credentials.create(fromPrivateKey);

            String fromAddress = getCurrentOwner(token, contractName );

            if (fromAddress == null) {
                response.error = true;
                response.errorMessage = "Cannot get owner of this NFT";
                return response;
            }

            if (!fromAddress.equalsIgnoreCase(fromCredentials.getAddress())) { //wrong private key
                response.error = true;
                response.errorMessage = "Private key is not matching owner";
                return response;
            }

            contract = getContract(fromCredentials, contractName); //call contract from the passed private key credentials

            TransactionReceipt tr = contract.safeTransferFrom(fromCredentials.getAddress(), toAddress, BigInteger.valueOf(token)).send(); //blocking call
        } catch (Exception e) {
            log.error("Transfer error");
            log.error("Transfer error", e);
            response.setError(true);
            response.setErrorMessage("Failed calling transfer Method");
        }
        return null;
    }

    public static class TransferResponse {
        @Getter
        @Setter
        boolean error = false;

        @Getter
        @Setter
        String errorMessage = "";
    }

}
