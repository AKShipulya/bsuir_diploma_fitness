package by.bsuir.fitness.command;

import by.bsuir.fitness.command.impl.ActivationCommand;
import by.bsuir.fitness.command.impl.GymPhotosCommand;
import by.bsuir.fitness.command.impl.HomePageCommand;
import by.bsuir.fitness.command.impl.LocaleCommand;
import by.bsuir.fitness.command.impl.LoginCommand;
import by.bsuir.fitness.command.impl.LoginPageCommand;
import by.bsuir.fitness.command.impl.LogoutCommand;
import by.bsuir.fitness.command.impl.NoAccessCommand;
import by.bsuir.fitness.command.impl.PasswordRestoreCommand;
import by.bsuir.fitness.command.impl.RegisterCommand;
import by.bsuir.fitness.command.impl.RegisterPageCommand;
import by.bsuir.fitness.command.impl.RestoreCommand;
import by.bsuir.fitness.command.impl.admin.AddCoachCommand;
import by.bsuir.fitness.command.impl.admin.AdminClientsCommand;
import by.bsuir.fitness.command.impl.admin.AdminCoachesCommand;
import by.bsuir.fitness.command.impl.admin.AdminCommentsCommand;
import by.bsuir.fitness.command.impl.admin.AdminExercisesCommand;
import by.bsuir.fitness.command.impl.admin.AdminOrdersCommand;
import by.bsuir.fitness.command.impl.admin.ChangeClientActiveCommand;
import by.bsuir.fitness.command.impl.admin.DeleteCommentCommand;
import by.bsuir.fitness.command.impl.admin.DeleteExerciseCommand;
import by.bsuir.fitness.command.impl.admin.FindClientsByFilterCommand;
import by.bsuir.fitness.command.impl.client.ChooseCoachCommand;
import by.bsuir.fitness.command.impl.client.ClientOrdersCommand;
import by.bsuir.fitness.command.impl.client.ClientProfileCommand;
import by.bsuir.fitness.command.impl.client.DeleteAccountCommand;
import by.bsuir.fitness.command.impl.client.FindCoachesCommand;
import by.bsuir.fitness.command.impl.client.RejectCoachCommand;
import by.bsuir.fitness.command.impl.client.ShowOrderPageCommand;
import by.bsuir.fitness.command.impl.client.UpdateMembershipCommand;
import by.bsuir.fitness.command.impl.coach.CoachClientsCommand;
import by.bsuir.fitness.command.impl.coach.ShowCommentsCommand;
import by.bsuir.fitness.command.impl.comment.AddCommentCommand;
import by.bsuir.fitness.command.impl.exercise.AddExerciseCommand;
import by.bsuir.fitness.command.impl.exercise.RejectExerciseCommand;
import by.bsuir.fitness.command.impl.exercise.ShowClientExercisesCommand;
import by.bsuir.fitness.command.impl.exercise.UpdateExerciseCommand;
import by.bsuir.fitness.command.impl.nutrition.AddNutritionCommand;
import by.bsuir.fitness.command.impl.nutrition.RejectNutritionCommand;
import by.bsuir.fitness.command.impl.nutrition.ShowClientNutritionCommand;
import by.bsuir.fitness.command.impl.nutrition.UpdateNutritionCommand;

/**
 * The enum Command enum.
 */
public enum CommandEnum {
    /**
     * The Locale.
     */
    LOCALE(new LocaleCommand()),
    /**
     * The Register.
     */
    REGISTER(new RegisterCommand()),
    /**
     * The Activate.
     */
    ACTIVATE(new ActivationCommand()),
    /**
     * The Restore.
     */
    RESTORE(new RestoreCommand()),
    /**
     * The Password restore.
     */
    PASSWORD_RESTORE(new PasswordRestoreCommand()),
    /**
     * The Home page.
     */
    HOME_PAGE(new HomePageCommand()),
    /**
     * The Client profile.
     */
    CLIENT_PROFILE(new ClientProfileCommand()),
    /**
     * The No access.
     */
    NO_ACCESS(new NoAccessCommand()),
    /**
     * The Gym photos.
     */
    GYM_PHOTOS(new GymPhotosCommand()),
    /**
     * The Update gym membership.
     */
    UPDATE_GYM_MEMBERSHIP(new UpdateMembershipCommand()),
    /**
     * The Show order page.
     */
    SHOW_ORDER_PAGE(new ShowOrderPageCommand()),
    /**
     * The Client orders.
     */
    CLIENT_ORDERS(new ClientOrdersCommand()),
    /**
     * The Find coaches.
     */
    FIND_COACHES(new FindCoachesCommand()),
    /**
     * The Choose coach.
     */
    CHOOSE_COACH(new ChooseCoachCommand()),
    /**
     * The Add comment.
     */
    ADD_COMMENT(new AddCommentCommand()),
    /**
     * The Reject coach.
     */
    REJECT_COACH(new RejectCoachCommand()),
    /**
     * The Add exercise.
     */
    ADD_EXERCISE(new AddExerciseCommand()),
    /**
     * The Reject exercise.
     */
    REJECT_EXERCISE(new RejectExerciseCommand()),
    /**
     * The Show client exercises.
     */
    SHOW_CLIENT_EXERCISES(new ShowClientExercisesCommand()),
    /**
     * The Update exercise.
     */
    UPDATE_EXERCISE(new UpdateExerciseCommand()),
    /**
     * The Show client nutrition.
     */
    SHOW_CLIENT_NUTRITION(new ShowClientNutritionCommand()),
    /**
     * The Update nutrition.
     */
    UPDATE_NUTRITION(new UpdateNutritionCommand()),
    /**
     * The Add nutrition.
     */
    ADD_NUTRITION(new AddNutritionCommand()),
    /**
     * The Reject nutrition.
     */
    REJECT_NUTRITION(new RejectNutritionCommand()),
    /**
     * The Coach clients.
     */
    COACH_CLIENTS(new CoachClientsCommand()),
    /**
     * The Coach comments.
     */
    COACH_COMMENTS(new ShowCommentsCommand()),
    /**
     * The Delete account.
     */
    DELETE_ACCOUNT(new DeleteAccountCommand()),
    /**
     * The Login user.
     */
    LOGIN_USER(new LoginCommand()),
    /**
     * The Logout user.
     */
    LOGOUT_USER(new LogoutCommand()),
    /**
     * The Show login page.
     */
    SHOW_LOGIN_PAGE(new LoginPageCommand()),
    /**
     * The Show register page.
     */
    SHOW_REGISTER_PAGE(new RegisterPageCommand()),
    /**
     * The Admin coaches.
     */
    ADMIN_COACHES(new AdminCoachesCommand()),
    /**
     * The Add coach.
     */
    ADD_COACH(new AddCoachCommand()),
    /**
     * The Admin clients.
     */
    ADMIN_CLIENTS(new AdminClientsCommand()),
    /**
     * The Admin exercises.
     */
    ADMIN_EXERCISES(new AdminExercisesCommand()),
    /**
     * The Delete exercise.
     */
    DELETE_EXERCISE(new DeleteExerciseCommand()),
    /**
     * The Change client active.
     */
    CHANGE_CLIENT_ACTIVE(new ChangeClientActiveCommand()),
    /**
     * The Admin comments.
     */
    ADMIN_COMMENTS(new AdminCommentsCommand()),
    /**
     * The Delete comment.
     */
    DELETE_COMMENT(new DeleteCommentCommand()),
    /**
     * The Admin orders.
     */
    ADMIN_ORDERS(new AdminOrdersCommand()),
    /**
     * The Find clients by filter.
     */
    FIND_CLIENTS_BY_FILTER(new FindClientsByFilterCommand());

    private ActionCommand command;

    CommandEnum(ActionCommand command) {
        this.command = command;
    }

    /**
     * Gets command.
     *
     * @return the command
     */
    public ActionCommand getCommand() {
        return command;
    }

    /**
     * Gets current command.
     *
     * @param action the action
     * @return the current command
     */
    public static ActionCommand getCurrentCommand(String action) {
        return CommandEnum.valueOf(action.toUpperCase()).command;
    }
}
