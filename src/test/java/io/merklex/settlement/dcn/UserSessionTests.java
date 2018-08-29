package io.merklex.settlement.dcn;


import com.greghaskins.spectrum.Spectrum;
import io.merklex.settlement.contracts.DCN;
import io.merklex.settlement.utils.Genesis;
import org.junit.runner.RunWith;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tuples.generated.Tuple7;

import java.math.BigInteger;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static io.merklex.settlement.utils.BetterAssert.assertEquals;

@RunWith(Spectrum.class)
public class UserSessionTests {
    {
        StaticNetwork.DescribeCheckpoint();

        DCN bob = StaticNetwork.DCN("bob");
        DCN henry = StaticNetwork.DCN("henry");

        Credentials bobKey = Genesis.GetKey("bob");
        Credentials henryKey = Genesis.GetKey("henry");
        Credentials merkleKey = Genesis.GetKey("merkle");

        BigInteger expireTime = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 1000);

        BigInteger startBalance = BigInteger.valueOf(1000000).multiply(BigInteger.TEN.pow(8));

        beforeAll(() -> {
            bob.add_user(bobKey.getAddress(), henryKey.getAddress()).send();
            bob.deposit_eth(BigInteger.valueOf(0), true, startBalance).send();
            StaticNetwork.DCN().add_exchange("merklex     ", merkleKey.getAddress()).send();

            BigInteger balance = bob.get_user_balance(BigInteger.valueOf(0), BigInteger.valueOf(0)).send();
            assertEquals(startBalance, balance);
        });

        describe("start session", () -> {
            System.out.println(expireTime);

            it("start session with id 123", () -> {
                bob.start_session(BigInteger.valueOf(123), BigInteger.valueOf(0),
                        BigInteger.valueOf(0), expireTime).send();
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

            describe("owner should not be able to close session", () -> {
                it("close session attempt", () -> {
                    bob.close_session(BigInteger.valueOf(123));
                });

                it("session should be open", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(expireTime, session.getValue5());
                });
            });

            describe("rando should not be able to close session", () -> {
                it("close session attempt", () -> {
                    henry.close_session(BigInteger.valueOf(123));
                });

                it("session should be open", () -> {
                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
                    session = bob.get_session(BigInteger.valueOf(123)).send();
                    assertEquals(expireTime, session.getValue5());
                });
            });

            // TODO
//            describe("exchange should not be able to close session", () -> {
//                it("close session", () -> {
//                    StaticNetwork.DCN().close_session(BigInteger.valueOf(123));
//                });
//
//                it("session should be closed", () -> {
//                    Tuple7<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger> session;
//                    session = bob.get_session(BigInteger.valueOf(123)).send();
//                    assertEquals(0, session.getValue5());
//                });
//            });
        });
    }
}
