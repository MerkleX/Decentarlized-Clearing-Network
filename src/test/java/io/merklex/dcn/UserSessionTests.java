package io.merklex.dcn;


import com.greghaskins.spectrum.Spectrum;
import io.merklex.dcn.contracts.DCN;
import io.merklex.dcn.utils.Genesis;
import io.merklex.dcn.utils.StaticNetwork;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;

import java.math.BigInteger;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static io.merklex.dcn.utils.BetterAssert.assertEquals;
import static io.merklex.dcn.utils.BetterAssert.assertNotEquals;

@RunWith(Spectrum.class)
public class UserSessionTests {
    {
        StaticNetwork.DescribeCheckpoint();

        DCN bob = StaticNetwork.DCN("bob");
        DCN henry = StaticNetwork.DCN("henry");

        Credentials bobKey = Genesis.GetKey("bob");
        Credentials henryKey = Genesis.GetKey("henry");
        Credentials merkleKey = Genesis.GetKey("merkle");

        BigInteger expireTime = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 50000);

        BigInteger startBalance = BigInteger.valueOf(1000000).multiply(BigInteger.TEN.pow(8));

        beforeAll(() -> {
            bob.add_user(henryKey.getAddress()).send();
            bob.deposit_eth(BigInteger.valueOf(0), true, startBalance).send();
            StaticNetwork.DCN().add_exchange("merklex     ", merkleKey.getAddress()).send();

            BigInteger balance = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
            assertEquals(startBalance, balance);
        });

        describe("start session", () -> {
            describe("expire time", () -> {
                it("expire time should not be too soon", () -> {
                    TransactionReceipt tx = bob.start_session(BigInteger.valueOf(123), BigInteger.valueOf(0),
                            BigInteger.valueOf(0), BigInteger.valueOf(System.currentTimeMillis() / 1000 + 100)).send();

                    List<Log> logs = tx.getLogs();
                    assertEquals(0, logs.size());

                    BigInteger expireTimeSet = bob.get_session(BigInteger.valueOf(123)).send().getValue5();
                    assertEquals(0, expireTimeSet);
                });

                it("expire time should not be too far in the future", () -> {
                    TransactionReceipt tx = bob.start_session(BigInteger.valueOf(123), BigInteger.valueOf(0),
                            BigInteger.valueOf(0), BigInteger.valueOf(System.currentTimeMillis() / 1000 + 2600000)).send();

                    List<Log> logs = tx.getLogs();
                    assertEquals(0, logs.size());

                    BigInteger expireTimeSet = bob.get_session(BigInteger.valueOf(123)).send().getValue5();
                    assertEquals(0, expireTimeSet);
                });
            });

            it("start session with id 123", () -> {
                TransactionReceipt tx = bob.start_session(BigInteger.valueOf(123), BigInteger.valueOf(0),
                        BigInteger.valueOf(0), expireTime).send();

                List<Log> logs = tx.getLogs();
                assertEquals(1, logs.size());

                List<DCN.SessionStartedEventResponse> events = bob.getSessionStartedEvents(tx);
                assertEquals(1, events.size());

                DCN.SessionStartedEventResponse event = events.get(0);
                assertEquals(123, event.session_id);
            });

            it("should be able to query session", () -> {
                Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                session = bob.get_session(BigInteger.valueOf(123)).send();

                assertEquals(0, /* positionCount */ session.getValue1());
                assertEquals(0, /* userId */ session.getValue2());
                assertEquals(0, /* exchangeId */ session.getValue3());
                assertEquals(0, /* maxEtherFees */ session.getValue4());
                assertEquals(expireTime, /* expireTime */ session.getValue5());
                assertEquals(henryKey.getAddress(), /* tradeAddress */ session.getValue6());
                assertEquals(0, /* etherBalance */ session.getValue7());
            });
        });

        describe("deposit into position", () -> {
            describe("other user should not be able to deposit", () -> {
                it("position deposit", () -> {
                    henry.position_deposit(BigInteger.valueOf(123), BigInteger.valueOf(0),
                            BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(1000)).send();
                });

                it("should not update balance", () -> {
                    BigInteger userBalance = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(startBalance, userBalance);

                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(0, /* etherBalance */ session.getValue7());
                });
            });

            describe("owner should be able to deposit eth", () -> {
                it("position deposit", () -> {
                    bob.position_deposit(BigInteger.valueOf(123), BigInteger.valueOf(0),
                            BigInteger.valueOf(0), BigInteger.valueOf(0), BigInteger.valueOf(1000)).send();
                });

                it("user balance should decrease", () -> {
                    BigInteger userBalance = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
                    assertEquals(startBalance.subtract(BigInteger.valueOf(1000).multiply(BigInteger.TEN.pow(8))), userBalance);
                });

                it("position balance should increase", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(1000, /* etherBalance */ session.getValue7());
                });
            });

            describe("should not be able to close active session", () -> {
                it("close session attempt", () -> {
                    henry.close_session(BigInteger.valueOf(123));
                });

                it("session should be active", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(expireTime, session.getValue5());
                });
            });

            describe("owner should not be able to end session", () -> {
                it("end session attempt", () -> {
                    bob.end_session(BigInteger.valueOf(123));
                });

                it("session should be active", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(expireTime, session.getValue5());
                });
            });

            describe("rando should not be able to close session", () -> {
                it("end session attempt", () -> {
                    henry.end_session(BigInteger.valueOf(123));
                });

                it("session should be active", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(expireTime, session.getValue5());
                });
            });

            describe("exchange should be able to end session", () -> {
                it("end session", () -> {
                    StaticNetwork.DCN().end_session(BigInteger.valueOf(123)).send();
                });

                it("session should be closed", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertNotEquals(expireTime, session.getValue5());

                    Assert.assertTrue(session.getValue5().compareTo(expireTime) < 0);
                    Assert.assertTrue(session.getValue5().compareTo(BigInteger.valueOf(System.currentTimeMillis() / 1000 + 1)) < 0);
                });
            });

            describe("close expired session", () -> {
                it("close session", () -> {
                    henry.close_session(BigInteger.valueOf(123)).send();
                });

                it("session should be closed", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(0, session.getValue5());
                });

                // TODO: check balances have been transferred over
            });
        });
    }
}
