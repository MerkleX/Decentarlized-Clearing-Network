//package io.merklex.dcn;
//
//
//import com.greghaskins.spectrum.Spectrum;
//import io.merklex.dcn.contracts.DCN;
//import io.merklex.dcn.contracts.ERC20;
//import io.merklex.dcn.utils.Accounts;
//import io.merklex.dcn.utils.StaticNetwork;
//import io.merklex.dnc.DCNEvents;
//import io.merklex.dnc.DCNResults;
//import io.merklex.dnc.models.GetSessionResult;
//import org.junit.Assert;
//import org.junit.runner.RunWith;
//import org.web3j.abi.EventValues;
//import org.web3j.crypto.Credentials;
//import org.web3j.protocol.core.methods.response.Log;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import java.math.BigInteger;
//import java.util.List;
//
//import static com.greghaskins.spectrum.dsl.specification.Specification.*;
//import static io.merklex.dcn.utils.BetterAssert.assertEquals;
//import static io.merklex.dcn.utils.BetterAssert.assertNotEquals;
//
//@RunWith(Spectrum.class)
//public class UserSessionTests {
//    {
//        StaticNetwork.DescribeCheckpoint();
//
//        DCN bob = StaticNetwork.DCN("bob");
//        DCN henry = StaticNetwork.DCN("henry");
//
//        Credentials jackKey = Accounts.GetKey("jack");
//
//        ERC20 token1;
//        ERC20 token2;
//        ERC20 token3;
//
//        try {
//            token1 = ERC20.deploy(StaticNetwork.Web3(), jackKey,
//                    BigInteger.ONE, new BigInteger(Accounts.getGasLimit()),
//                    BigInteger.valueOf(100000000000L),
//                    "token1", BigInteger.valueOf(16), "TK1").send();
//            token2 = ERC20.deploy(StaticNetwork.Web3(), jackKey,
//                    BigInteger.ONE, new BigInteger(Accounts.getGasLimit()),
//                    BigInteger.valueOf(100000000000L),
//                    "token2", BigInteger.valueOf(16), "TK2").send();
//            token3 = ERC20.deploy(StaticNetwork.Web3(), jackKey,
//                    BigInteger.ONE, new BigInteger(Accounts.getGasLimit()),
//                    BigInteger.valueOf(100000000000L),
//                    "token3", BigInteger.valueOf(16), "TK3").send();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        Credentials bobKey = Accounts.GetKey("bob");
//        Credentials henryKey = Accounts.GetKey("henry");
//        Credentials merkleKey = Accounts.GetKey("merkle");
//
//        BigInteger expireTime = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 50000);
//
//        BigInteger startBalance = BigInteger.valueOf(1000000).multiply(BigInteger.TEN.pow(8));
//
//        beforeAll(() -> {
//            bob.executeTransaction(DCN.add_user(henryKey.getAddress()));
//            bob.executeTransaction(DCN.deposit_eth(BigInteger.valueOf(0), true), startBalance);
//            StaticNetwork.DCN().executeTransaction(DCN.add_exchange("merklex     ", merkleKey.getAddress()));
//
//            BigInteger balance = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0));
//            assertEquals(startBalance, balance);
//
//            StaticNetwork.DCN().executeTransaction(DCN.add_asset("TK-1", BigInteger.valueOf(1000), token1.getContractAddress()));
//            StaticNetwork.DCN().executeTransaction(DCN.add_asset("TK-2", BigInteger.valueOf(10000), token2.getContractAddress()));
//            StaticNetwork.DCN().executeTransaction(DCN.add_asset("TK-3", BigInteger.valueOf(100000), token3.getContractAddress()));
//        });
//
//        BigInteger sessionId = BigInteger.valueOf(123);
//        describe("start session", () -> {
//            describe("expire time", () -> {
//                it("expire time should not be too soon", () -> {
//                    TransactionReceipt tx = bob.executeTransaction(DCN.start_session(sessionId, BigInteger.valueOf(0),
//                            BigInteger.valueOf(0), BigInteger.valueOf(System.currentTimeMillis() / 1000 + 100)));
//
//                    List<Log> logs = tx.getLogs();
//                    assertEquals(0, logs.size());
//
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(0, session.expireTime);
//                });
//
//                it("expire time should not be too far in the future", () -> {
//                    TransactionReceipt tx = bob.executeTransaction(DCN.start_session(sessionId, BigInteger.valueOf(0),
//                            BigInteger.valueOf(0), BigInteger.valueOf(System.currentTimeMillis() / 1000 + 2600000)));
//
//                    List<Log> logs = tx.getLogs();
//                    assertEquals(0, logs.size());
//
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(0, session.expireTime);
//                });
//            });
//
//            it("start session with id 123", () -> {
//                TransactionReceipt tx = bob.executeTransaction(DCN.start_session(sessionId, BigInteger.valueOf(0),
//                        BigInteger.valueOf(0), expireTime));
//
//                List<Log> logs = tx.getLogs();
//                assertEquals(1, logs.size());
//
//                List<DCN.SessionStartedEventResponse> events = bob.getSessionStartedEvents(tx);
//                assertEquals(1, events.size());
//
//                DCN.SessionStartedEventResponse event = events.get(0);
//                assertEquals(123, event.session_id);
//            });
//
//            it("should be able to query session", () -> {
//                GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//
//                assertEquals(1, session.turnOver);
//                assertEquals(0, session.positionCount);
//                assertEquals(0, session.userId);
//                assertEquals(0, session.exchangeId);
//                assertEquals(0, session.maxEtherFees);
//                assertEquals(expireTime, session.expireTime);
//                assertEquals(henryKey.getAddress(), session.tradeAddress);
//                assertEquals(0, session.etherBalance);
//            });
//        });
//
//        describe("deposit into position", () -> {
//            describe("other user should not be able to deposit", () -> {
//                it("position deposit", () -> {
//                    TransactionReceipt send = henry.executeTransaction(DCN.position_deposit(sessionId, BigInteger.valueOf(0),
//                            BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(1000)));
//                    assertEquals(1, send.getLogs().size());
//                });
//
//                it("should not update balance", () -> {
//                    BigInteger userBalance = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0));
//                    assertEquals(startBalance, userBalance);
//
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(0, /* etherBalance */ session.etherBalance);
//                });
//            });
//
//            describe("owner should be able to deposit eth", () -> {
//                it("position deposit", () -> {
//                    TransactionReceipt send = bob.executeTransaction(DCN.position_deposit(sessionId, BigInteger.valueOf(0),
//                            BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(1000)));
//
//                    assertEquals(2, send.getLogs().size());
//                    assertEquals("0x00", send.getLogs().get(1).getData());
//
//                    List<DCN.PositionDepositEventResponse> events = DCNEvents.ExtractPositionDeposits(send);
//                    assertEquals(1, events.size());
//
//                    DCN.PositionDepositEventResponse depositEvent = events.get(0);
//                    assertEquals(sessionId, depositEvent.session_id);
//                    assertEquals(1, depositEvent.session_turnover);
//                    assertEquals(0, depositEvent.position_id);
//                    assertEquals(1000, depositEvent.quantity);
//                });
//
//                it("user balance should decrease", () -> {
//                    BigInteger userBalance = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0));
//                    assertEquals(startBalance.subtract(BigInteger.valueOf(1000).multiply(BigInteger.TEN.pow(10))), userBalance);
//                });
//
//                it("position balance should increase", () -> {
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(1000, /* etherBalance */ session.etherBalance);
//                });
//            });
//
//            describe("should be able to add position", () -> {
//                StaticNetwork.DescribeCheckpointForEach();
//
//                it("should create new session with zero balance", () -> {
//                    TransactionReceipt send = bob.executeTransaction(DCN.position_deposit(sessionId, BigInteger.ZERO,
//                            BigInteger.valueOf(1), BigInteger.valueOf(1), BigInteger.ZERO));
//
//                    assertEquals(3, send.getLogs().size());
//
//                    DCN.PositionAddedEventResponse positionAdded = DCNEvents.ExtractPositionAddedEvents(send).get(0);
//                    assertEquals(sessionId, positionAdded.session_id);
//
//                    DCN.PositionDepositEventResponse positionDeposit = DCNEvents.ExtractPositionDeposits(send).get(0);
//                    assertEquals(sessionId, positionDeposit.session_id);
//                    assertEquals(1, positionDeposit.position_id);
//                    assertEquals(1, positionDeposit.session_turnover);
//                    assertEquals(0, positionDeposit.quantity);
//
//                    /* Second event should be response code from add position */
//                    assertEquals("0x00", send.getLogs().get(2).getData());
//
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(1, session.positionCount);
//                });
//
//                it("should not be able to create position without balance", () -> {
//                    TransactionReceipt send = bob.executeTransaction(DCN.position_deposit(sessionId, BigInteger.ZERO,
//                            BigInteger.valueOf(1), BigInteger.valueOf(1), BigInteger.valueOf(1000)));
//                    Assert.assertEquals("0x04", send.getLogs().get(0).getData());
//
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(0, session.positionCount);
//                });
//
//                it("should be able to create position with balance", () -> {
//                    token1.transfer(bobKey.getAddress(), BigInteger.valueOf(100000)).send();
//                    ERC20 bobERC = ERC20.load(token1.getContractAddress(), StaticNetwork.Web3(), bobKey, BigInteger.ONE, new BigInteger(Accounts.getGasLimit()));
//                    bobERC.approve(bob.getContractAddress(), BigInteger.valueOf(1000)).send();
//                    bob.executeTransaction(DCN.deposit_asset(BigInteger.valueOf(0), BigInteger.valueOf(1), BigInteger.valueOf(1000)));
//
//                    TransactionReceipt send = bob.executeTransaction(DCN.position_deposit(sessionId, BigInteger.ZERO,
//                            BigInteger.valueOf(1), BigInteger.valueOf(1), BigInteger.valueOf(1)));
//
//                    assertEquals(3, send.getLogs().size());
//
//                    DCN.PositionAddedEventResponse positionAdded = DCNEvents.ExtractPositionAddedEvents(send).get(0);
//                    assertEquals(sessionId, positionAdded.session_id);
//
//                    DCN.PositionDepositEventResponse positionDeposit = DCNEvents.ExtractPositionDeposits(send).get(0);
//                    assertEquals(sessionId, positionDeposit.session_id);
//                    assertEquals(1, positionDeposit.position_id);
//                    assertEquals(1, positionDeposit.session_turnover);
//                    assertEquals(1, positionDeposit.quantity);
//
//                    assertEquals("0x00", send.getLogs().get(2).getData());
//
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(1, session.positionCount);
//
//                    send = bob.executeTransaction(DCN.position_deposit(sessionId, BigInteger.ZERO,
//                            BigInteger.valueOf(1), BigInteger.valueOf(1), BigInteger.valueOf(1)));
//                    Assert.assertEquals("0x04", send.getLogs().get(0).getData());
//
//                    DCNResults.GetSession(session, bob.get_session(sessionId));
//                    assertEquals(1, session.positionCount);
//                });
//            });
//
//            describe("should not be able to close active session", () -> {
//                it("close session attempt", () -> {
//                    henry.executeTransaction(DCN.close_session(sessionId));
//                });
//
//                it("session should be active", () -> {
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(expireTime, session.expireTime);
//                });
//            });
//
//            describe("owner should not be able to end session", () -> {
//                it("end session attempt", () -> {
//                    henry.executeTransaction(DCN.close_session(sessionId));
//                });
//
//                it("session should be active", () -> {
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(expireTime, session.expireTime);
//                });
//            });
//
//            describe("rando should not be able to close session", () -> {
//                it("end session attempt", () -> {
//                    henry.executeTransaction(DCN.close_session(sessionId));
//                });
//
//                it("session should be active", () -> {
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(expireTime, session.expireTime);
//                });
//            });
//
//            describe("exchange should be able to end session", () -> {
//                it("end session", () -> {
//                    StaticNetwork.DCN().executeTransaction(DCN.end_session(sessionId));
//                });
//
//                it("session should be closed", () -> {
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertNotEquals(expireTime, session.expireTime);
//
//                    Assert.assertTrue(session.expireTime.compareTo(expireTime) < 0);
//                    Assert.assertTrue(session.expireTime.compareTo(BigInteger.valueOf(System.currentTimeMillis() / 1000 + 1)) < 0);
//                });
//            });
//
//            describe("close expired session", () -> {
//                it("close session", () -> {
//                    henry.executeTransaction(DCN.close_session(sessionId));
//                });
//
//                it("session should be closed", () -> {
//                    GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                    assertEquals(0, session.expireTime);
//                });
//
//                // TODO: check balances have been transferred over
//            });
//
//            it("session in same position should have an increased turnover", () -> {
//                BigInteger nextExpireTime = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 50000);
//                TransactionReceipt tx = bob.executeTransaction(DCN.start_session(sessionId, BigInteger.valueOf(0),
//                        BigInteger.valueOf(0), nextExpireTime));
//
//                List<DCN.SessionStartedEventResponse> sessionStartedEvents = bob.getSessionStartedEvents(tx);
//                assertEquals(1, sessionStartedEvents.size());
//                assertEquals(sessionId, sessionStartedEvents.get(0).session_id);
//
//                GetSessionResult session = DCNResults.GetSession(new GetSessionResult(), bob.get_session(sessionId));
//                assertEquals(2, session.turnOver);
//                assertEquals(nextExpireTime, session.expireTime);
//            });
//        });
//    }
//}
