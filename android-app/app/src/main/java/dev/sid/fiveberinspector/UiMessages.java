package dev.sid.fiveberinspector;
import android.content.Context;
/** Presentation only: protocol/state keys and user/operator data are never translated. */
final class UiMessages {
 static String known(Context context,String message){
  if(message==null)return "";
  return switch(message){
   case "The system denies access to the eUICC applet. Starting the computer connection alone cannot grant missing card access on this ROM.": yield context.getString(R.string.ui_the_system_denies_access_to_the_euicc_applet_starting_the_comput);
   case "No accessible card service was found. Check the compatibility results for HAL, Telephony and OMAPI.": yield context.getString(R.string.ui_no_accessible_card_service_was_found_check_the_compatibility_res);
   case "Required system interface classes or methods are unavailable. Check the result for each card channel.": yield context.getString(R.string.ui_required_system_interface_classes_or_methods_are_unavailable_che);
   case "No readable eUICC was found. Check the card and this ROM's Secure Element access policy.": yield context.getString(R.string.ui_no_readable_euicc_was_found_check_the_card_and_this_rom_s_secure);
   case "More than one readable eUICC was found. Choose the intended card before continuing.": yield context.getString(R.string.ui_more_than_one_readable_euicc_was_found_choose_the_intended_card);
   case "The explicitly selected card is missing or changed. Choose and verify a card again; no alternative was selected automatically.": yield context.getString(R.string.ui_the_explicitly_selected_card_is_missing_or_changed_choose_and_ve);
   case "A card channel could not be closed. Stop and verify the card connection before continuing.": yield context.getString(R.string.ui_a_card_channel_could_not_be_closed_stop_and_verify_the_card_conn);
   case "The system reader inventory is invalid or exceeds this build's bounds. Automatic selection stopped.": yield context.getString(R.string.ui_the_system_reader_inventory_is_invalid_or_exceeds_this_build_s_b);
   case "The checked readers reported no card. Check the inserted card and the reader routing.": yield context.getString(R.string.ui_the_checked_readers_reported_no_card_check_the_inserted_card_and);
   case "The ISD-R management applet could not be opened on the checked readers.": yield context.getString(R.string.ui_the_isd_r_management_applet_could_not_be_opened_on_the_checked_r);
   case "The card did not return a verifiable complete eUICC identity.": yield context.getString(R.string.ui_the_card_did_not_return_a_verifiable_complete_euicc_identity);
   case "A card service could not be reached; it may be absent or blocked by system policy.": yield context.getString(R.string.ui_a_card_service_could_not_be_reached_it_may_be_absent_or_blocked);
   case "This operation is unavailable on the selected card connection.": yield context.getString(R.string.ui_this_operation_is_unavailable_on_the_selected_card_connection);
   case "No notification attempt recorded.": yield context.getString(R.string.ui_no_notification_attempt_recorded);
   case "Operator acknowledged the notification.": yield context.getString(R.string.ui_operator_acknowledged_the_notification);
   case "Operator acknowledged the notification; local cleanup needs review. Do not reinstall.": yield context.getString(R.string.ui_operator_acknowledged_the_notification_local_cleanup_needs_revie);
   case "Delivery could not be confirmed. Contact the operator before retrying; do not reinstall.": yield context.getString(R.string.ui_delivery_could_not_be_confirmed_contact_the_operator_before_retr);
   case "The notification destination is not supported by this build. Contact the operator; do not reinstall.": yield context.getString(R.string.ui_the_notification_destination_is_not_supported_by_this_build_cont);
   case "Notification was not sent. Check the connection and contact the operator; do not reinstall.": yield context.getString(R.string.ui_notification_was_not_sent_check_the_connection_and_contact_the_o);
   case "Notification status is unavailable. Contact the operator if service is not ready; do not reinstall.": yield context.getString(R.string.ui_notification_status_is_unavailable_contact_the_operator_if_servi);
   case "Notification delivery is not confirmed. Do not reinstall to retry it.": yield context.getString(R.string.ui_notification_delivery_is_not_confirmed_do_not_reinstall_to_retry);
   case "Computer detected. Starting the local service…": yield context.getString(R.string.ui_computer_detected_starting_the_local_service);
   case "Open 8bercueLa → Connect using computer (or Start connection again), then run the command within two minutes.": yield context.getString(R.string.ui_open_8bercue_connect_using_computer_or_start_connection_again_th);
   case "The app changed during connection. Reopen 8bercueLa, start a new connection window and run the command again.": yield context.getString(R.string.ui_the_app_changed_during_connection_reopen_8bercue_start_a_new_con);
   case "The connection window changed. Use Start connection again, then rerun the command.": yield context.getString(R.string.ui_the_connection_window_changed_use_start_connection_again_then_re);
   case "The app closed during connection. Reopen 8bercueLa and start the connection again.": yield context.getString(R.string.ui_the_app_closed_during_connection_reopen_8bercue_and_start_the_co);
   case "Connection was not confirmed. Check the app before retrying; a late connection may still appear.": yield context.getString(R.string.ui_connection_was_not_confirmed_check_the_app_before_retrying_a_lat);
   case "The local service could not start. Keep 8bercueLa open, start a new connection window and retry. If it repeats, check system restrictions in Settings → Compatibility check.": yield context.getString(R.string.ui_the_local_service_could_not_start_keep_8bercue_open_start_a_new);
   case "This build needs Android 9 or later.": yield context.getString(R.string.ui_this_build_needs_android_9_or_later);
   case "This build needs an ARM64 phone for card management.": yield context.getString(R.string.ui_this_build_needs_an_arm64_phone_for_card_management);
   case "Invalid SM-DP+ address": yield context.getString(R.string.ui_invalid_sm_dp_address);
   case "Use the operator's SM-DP+ domain name": yield context.getString(R.string.ui_use_the_operator_s_sm_dp_domain_name);
   case "Invalid SM-DP+ domain name": yield context.getString(R.string.ui_invalid_sm_dp_domain_name);
   case "Invalid activation code length": yield context.getString(R.string.ui_invalid_activation_code_length);
   case "Use a GSMA format 1 activation code": yield context.getString(R.string.ui_use_a_gsma_format_1_activation_code);
   case "Missing or unsupported matching ID": yield context.getString(R.string.ui_missing_or_unsupported_matching_id);
   case "Activation codes with an SM-DP+ OID are not supported by this engine": yield context.getString(R.string.ui_activation_codes_with_an_sm_dp_oid_are_not_supported_by_this_eng);
   case "Invalid confirmation-code flag": yield context.getString(R.string.ui_invalid_confirmation_code_flag);
   case "This activation code requires a confirmation code": yield context.getString(R.string.ui_this_activation_code_requires_a_confirmation_code);
   case "Invalid confirmation code": yield context.getString(R.string.ui_invalid_confirmation_code);
   case "Management channel opened, complete eUICC identity verified and owned channel closed.": yield context.getString(R.string.ui_management_channel_opened_complete_euicc_identity_verified_and_o);
   case "Service/reader discovery": yield context.getString(R.string.ui_service_reader_discovery);
   case "Service lookup": yield context.getString(R.string.ui_service_lookup);
   case "Card presence": yield context.getString(R.string.ui_card_presence);
   case "Opening ISD-R": yield context.getString(R.string.ui_opening_isd_r);
   case "Reading eUICC identity": yield context.getString(R.string.ui_reading_euicc_identity);
   case "Closing owned channel": yield context.getString(R.string.ui_closing_owned_channel);
   case "Card selection": yield context.getString(R.string.ui_card_selection);
   case "no accessible service/readers found": yield context.getString(R.string.ui_no_accessible_service_readers_found);
   case "required interface classes or methods are unavailable": yield context.getString(R.string.ui_required_interface_classes_or_methods_are_unavailable);
   case "Shell access denied by system policy or card access rules": yield context.getString(R.string.ui_shell_access_denied_by_system_policy_or_card_access_rules);
   case "reader inventory is invalid or exceeds this build's bounds; automatic selection stopped": yield context.getString(R.string.ui_reader_inventory_is_invalid_or_exceeds_this_build_s_bounds_autom);
   case "no card reported on this reader": yield context.getString(R.string.ui_no_card_reported_on_this_reader);
   case "management applet unavailable or channel could not be opened": yield context.getString(R.string.ui_management_applet_unavailable_or_channel_could_not_be_opened);
   case "response did not verify a complete eUICC identity": yield context.getString(R.string.ui_response_did_not_verify_a_complete_euicc_identity);
   case "service could not be reached; it may be absent or lookup may be blocked": yield context.getString(R.string.ui_service_could_not_be_reached_it_may_be_absent_or_lookup_may_be_b);
   case "the selected card identity or reader changed; no automatic fallback": yield context.getString(R.string.ui_the_selected_card_identity_or_reader_changed_no_automatic_fallba);
   case "multiple readable eUICCs; automatic selection stopped": yield context.getString(R.string.ui_multiple_readable_euiccs_automatic_selection_stopped);
   case "cleanup could not be verified; all fallback stopped": yield context.getString(R.string.ui_cleanup_could_not_be_verified_all_fallback_stopped);
   case "check could not complete": yield context.getString(R.string.ui_check_could_not_complete);
   default: yield message;
  };
 }
 static String compatibility(Context context,String report){
  if(!AppLanguage.isChinese(context))return report;
  String result=report;
  result=result.replace("reader inventory is invalid or exceeds this build's bounds; automatic selection stopped", context.getString(R.string.ui_reader_inventory_is_invalid_or_exceeds_this_build_s_bounds_autom));
  result=result.replace("Management channel opened, complete eUICC identity verified and owned channel closed.", context.getString(R.string.ui_management_channel_opened_complete_euicc_identity_verified_and_o));
  result=result.replace("service could not be reached; it may be absent or lookup may be blocked", context.getString(R.string.ui_service_could_not_be_reached_it_may_be_absent_or_lookup_may_be_b));
  result=result.replace("the selected card identity or reader changed; no automatic fallback", context.getString(R.string.ui_the_selected_card_identity_or_reader_changed_no_automatic_fallba));
  result=result.replace("management applet unavailable or channel could not be opened", context.getString(R.string.ui_management_applet_unavailable_or_channel_could_not_be_opened));
  result=result.replace("Shell access denied by system policy or card access rules", context.getString(R.string.ui_shell_access_denied_by_system_policy_or_card_access_rules));
  result=result.replace("required interface classes or methods are unavailable", context.getString(R.string.ui_required_interface_classes_or_methods_are_unavailable));
  result=result.replace("multiple readable eUICCs; automatic selection stopped", context.getString(R.string.ui_multiple_readable_euiccs_automatic_selection_stopped));
  result=result.replace("cleanup could not be verified; all fallback stopped", context.getString(R.string.ui_cleanup_could_not_be_verified_all_fallback_stopped));
  result=result.replace("response did not verify a complete eUICC identity", context.getString(R.string.ui_response_did_not_verify_a_complete_euicc_identity));
  result=result.replace("no accessible service/readers found", context.getString(R.string.ui_no_accessible_service_readers_found));
  result=result.replace("no card reported on this reader", context.getString(R.string.ui_no_card_reported_on_this_reader));
  result=result.replace("Service/reader discovery", context.getString(R.string.ui_service_reader_discovery));
  result=result.replace("check could not complete", context.getString(R.string.ui_check_could_not_complete));
  result=result.replace("Reading eUICC identity", context.getString(R.string.ui_reading_euicc_identity));
  result=result.replace("Closing owned channel", context.getString(R.string.ui_closing_owned_channel));
  result=result.replace("Service lookup", context.getString(R.string.ui_service_lookup));
  result=result.replace("Card selection", context.getString(R.string.ui_card_selection));
  result=result.replace("Card presence", context.getString(R.string.ui_card_presence));
  result=result.replace("Opening ISD-R", context.getString(R.string.ui_opening_isd_r));
  return result;
 }
 private UiMessages(){}
}
