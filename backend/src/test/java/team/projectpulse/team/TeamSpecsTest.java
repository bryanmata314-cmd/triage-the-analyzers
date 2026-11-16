package team.projectpulse.team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * The team name search compares {@code lower(teamName)} in the database with a search term lowercased in Java,
 * so the Java side must not depend on the server's default locale: in a Turkish locale {@code "I".toLowerCase()}
 * is a dotless {@code "ı"}, which the database's lowercased team names never contain.
 */
@ExtendWith(MockitoExtension.class)
class TeamSpecsTest {

    @Mock
    Root<Team> root;

    @Mock
    CriteriaQuery<?> query;

    @Mock
    CriteriaBuilder criteriaBuilder;

    @Mock
    Path<String> teamName;

    @Mock
    Expression<String> loweredTeamName;

    private Locale originalLocale;

    @BeforeEach
    void useTurkishDefaultLocale() {
        this.originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
    }

    @AfterEach
    void restoreDefaultLocale() {
        Locale.setDefault(this.originalLocale);
    }

    @Test
    void containsTeamNameLowercasesTheSearchTermIndependentlyOfTheDefaultLocale() {
        given(this.root.<String>get("teamName")).willReturn(this.teamName);
        given(this.criteriaBuilder.lower(this.teamName)).willReturn(this.loweredTeamName);

        TeamSpecs.containsTeamName("TITAN").toPredicate(this.root, this.query, this.criteriaBuilder);

        then(this.criteriaBuilder).should().like(this.loweredTeamName, "%titan%");
    }

}
